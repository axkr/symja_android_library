package org.matheclipse.core.builtin;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.convert.RGBColor;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.Dimensions2D;
import org.matheclipse.core.graphics.IGraphics3D;
import org.matheclipse.core.graphics.Show2SVG;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

public class GraphicsFunctions {
  private static final Logger LOGGER = LogManager.getLogger();

  private static final DecimalFormatSymbols US_SYMBOLS = new DecimalFormatSymbols(Locale.US);
  protected static final DecimalFormat FORMATTER = new DecimalFormat("0.0####", US_SYMBOLS);


  /** Default plot style colors for functions */
  static final RGBColor[] PLOT_COLORS = new RGBColor[] { //
      new RGBColor(0.368417f, 0.506779f, 0.709798f), //
      new RGBColor(0.880722f, 0.611041f, 0.142051f), //
      new RGBColor(0.560181f, 0.691569f, 0.194885f), //
      new RGBColor(0.922526f, 0.385626f, 0.209179f), //
      new RGBColor(0.528488f, 0.470624f, 0.701351f), //
      new RGBColor(0.772079f, 0.431554f, 0.102387f), //
      new RGBColor(0.363898f, 0.618501f, 0.782349f), //
      new RGBColor(1.0f, 0.75f, 0.0f), //
      new RGBColor(0.647624f, 0.37816f, 0.614037f), //
      new RGBColor(0.571589f, 0.586483f, 0.0f), //
      new RGBColor(0.915f, 0.3325f, 0.2125f), //
      new RGBColor(0.40082222609352647f, 0.5220066643438841f, 0.85f), //
      new RGBColor(0.9728288904374106f, 0.621644452187053f, 0.07336199581899142f), //
      new RGBColor(0.736782672705901f, 0.358f, 0.5030266573755369f), //
      new RGBColor(0.28026441037696703f, 0.715f, 0.4292089322474965f) //
  };

  private static class Arrow extends AbstractEvaluator implements IGraphics3D {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }

    @Override
    public boolean graphics3D(StringBuilder buf, IAST ast, IAST color, IExpr opacity) {
      if (ast.argSize() > 0 && ast.arg1().isList()) {
        IAST list = (IAST) ast.arg1();
        buf.append("{type: \'arrow\',");
        setColor(buf, color, F.NIL, true);
        setOpacity(buf, opacity.orElse(F.C1));
        if (list.isListOfLists() && graphics3DCoords(buf, list)) {
          buf.append("}");
          return true;
        }
      }
      return false;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class BernsteinBasis extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr dArg1 = ast.arg1();
      IExpr nArg2 = ast.arg2();
      IExpr x = ast.arg3();
      if (dArg1.isReal() && nArg2.isReal() && x.isReal()) {
        int d = dArg1.toIntDefault();
        if (d < 0) {
          // Non-negative machine-sized integer expected at position `2` in `1`.
          return IOFunctions.printMessage(ast.topHead(), "intnm", F.list(ast, F.C1), engine);
        }
        IInteger di = F.ZZ(d);
        int n = nArg2.toIntDefault();
        if (n < 0) {
          // Non-negative machine-sized integer expected at position `2` in `1`.
          return IOFunctions.printMessage(ast.topHead(), "intnm", F.list(ast, F.C1), engine);
        }
        if (n > d) {
          // Index `1` should be a machine sized integer between `2` and `3`.
          return IOFunctions.printMessage(ast.topHead(), "invidx2", F.list(nArg2, F.C0, di),
              engine);
        }
        if (engine.evalLess(F.C0, x, F.C1)) {
          IInteger ni = F.ZZ(n);
          // Binomial(d, ni) * x^ni * (1 - x)^(di - ni)
          return F.Times(F.Binomial(di, ni), F.Power(x, ni),
              F.Power(F.Subtract(F.C1, x), F.Subtract(di, ni)));
        } else {
          return F.C0;
        }
        // return F.Piecewise(F.list1(F.list2(piece, condition)), F.C0);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NHOLDALL | ISymbol.NUMERICFUNCTION);
    }
  }

  private static class Circle extends AbstractEvaluator implements IGraphics3D {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }

    @Override
    public boolean graphics2DDimension(IAST ast, Dimensions2D dim) {
      if (ast.size() == 1) {
        double cx = 1.0;
        double cy = 1.0;
        double rx = 1.0;
        double ry = 1.0;
        dim.minMax(cx - rx, cx + rx, cy - ry, cy + ry);
        return true;
      } else if (ast.size() == 2) {
        if (ast.arg1().isAST(S.List, 3)) {
          IAST list1 = (IAST) ast.arg1();

          double cx = ((ISignedNumber) list1.arg1()).doubleValue();
          double cy = ((ISignedNumber) list1.arg2()).doubleValue();
          double rx = 1.0;
          double ry = 1.0;

          dim.minMax(cx - rx, cx + rx, cy - ry, cy + ry);
          return true;
        }
      } else if (ast.size() == 3 && ast.arg1().isAST(S.List, 3) && ast.arg2().isAST(S.List, 3)) {
        IAST list1 = (IAST) ast.arg1();
        IAST list2 = (IAST) ast.arg2();

        double cx = ((ISignedNumber) list1.arg1()).doubleValue();
        double cy = ((ISignedNumber) list1.arg2()).doubleValue();
        double rx = ((ISignedNumber) list2.arg1()).doubleValue();
        double ry = ((ISignedNumber) list2.arg2()).doubleValue();

        dim.minMax(cx - rx, cx + rx, cy - ry, cy + ry);
        return true;
      }

      return false;
    }

    @Override
    public boolean graphics2D(StringBuilder buf, IAST ast, Dimensions2D dim, IAST color,
        IExpr opacity) {
      try {

        // <svg width=\"350.66666666666674px\" height=\"350.66666666666674px\"
        // xmlns:svg=\"http://www.w3.org/2000/svg\"
        // xmlns=\"http://www.w3.org/2000/svg\"
        // version=\"1.1\
        // viewBox=\"-0.333333 -0.333333 350.666667 350.666667\">
        // <ellipse cx=\"175.000000\" cy=\"175.000000\" rx=\"175.000000\" ry=\"175.000000\"
        // style=\"stroke: rgb(0.000000%, 0.000000%, 0.000000%); stroke-opacity: 1; stroke-width:
        // 0.666667px; fill: none; opacity: 1.0\" />"

        int width = dim.width;
        int height = dim.height;
        double xMin = dim.xMin;
        double xMax = dim.xMax;
        double yMin = dim.yMin;
        double yMax = dim.yMax;
        if (ast.size() == 1) {
          buf.append("<ellipse ");
          double xAxisScalingFactor = width / (xMax - xMin);
          double yAxisScalingFactor = height / (yMax - yMin);

          double cx = 1.0;
          double cy = 1.0;
          double rx = 1.0;
          double ry = 1.0;
          // x="0.000000" y="0.000000" width="350.000000" height="350.000000"
          buf.append("cx=\"");
          buf.append(FORMATTER.format(cx * xAxisScalingFactor));
          buf.append("\" cy=\"");
          buf.append(FORMATTER.format(cy * yAxisScalingFactor));
          buf.append("\" rx=\"");
          buf.append(FORMATTER.format(rx * xAxisScalingFactor));
          buf.append("\" ry=\"");
          buf.append(FORMATTER.format(ry * yAxisScalingFactor));
          return true;

        } else if (ast.size() == 2) {
          if (ast.arg1().isAST(S.List, 3)) {
            IAST list1 = (IAST) ast.arg1();
            buf.append("<ellipse ");
            double xAxisScalingFactor = width / (xMax - xMin);
            double yAxisScalingFactor = height / (yMax - yMin);

            double cx = ((ISignedNumber) list1.arg1()).doubleValue();
            double cy = ((ISignedNumber) list1.arg2()).doubleValue();
            double rx = 1.0;
            double ry = 1.0;
            // x="0.000000" y="0.000000" width="350.000000" height="350.000000"
            buf.append("cx=\"");
            buf.append(FORMATTER.format(cx * xAxisScalingFactor));
            buf.append("\" cy=\"");
            buf.append(FORMATTER.format(cy * yAxisScalingFactor));
            buf.append("\" rx=\"");
            buf.append(FORMATTER.format(rx * xAxisScalingFactor));
            buf.append("\" ry=\"");
            buf.append(FORMATTER.format(ry * yAxisScalingFactor));
            return true;
          }
        } else if (ast.size() == 3 && ast.arg1().isAST(S.List, 3) && ast.arg2().isAST(S.List, 3)) {
          IAST list1 = (IAST) ast.arg1();
          IAST list2 = (IAST) ast.arg2();
          buf.append("<ellipse ");
          double xAxisScalingFactor = width / (xMax - xMin);
          double yAxisScalingFactor = height / (yMax - yMin);

          double cx = ((ISignedNumber) list1.arg1()).doubleValue();
          double cy = ((ISignedNumber) list1.arg2()).doubleValue();
          double rx = ((ISignedNumber) list2.arg1()).doubleValue();
          double ry = ((ISignedNumber) list2.arg2()).doubleValue();

          buf.append("cx=\"");
          buf.append(FORMATTER.format(cx * xAxisScalingFactor));
          buf.append("\" cy=\"");
          buf.append(FORMATTER.format(cy * yAxisScalingFactor));
          buf.append("\" rx=\"");
          buf.append(FORMATTER.format(rx * xAxisScalingFactor));
          buf.append("\" ry=\"");
          buf.append(FORMATTER.format(ry * yAxisScalingFactor));
          return true;
        }

      } catch (RuntimeException ex) {
        // catch cast exceptions for example
        LOGGER.error("Circle.graphics2D() failed", ex);
      } finally {
        buf.append("\" \n      style=\"stroke: none; stroke-width: 0.000000px; ");
        buf.append("fill: rgb(");
        dim.getColorRGB(buf);
        buf.append("); ");
        buf.append("stroke-opacity: 1; stroke-width: 0.666667px; fill-opacity: 1\" />\n");
      }
      return false;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class Cuboid extends AbstractEvaluator implements IGraphics3D {

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
    public boolean graphics3D(StringBuilder buf, IAST ast, IAST color, IExpr opacity) {
      // {
      // type: 'cuboid',
      // color: [0.5, 0.5, 0.5],
      // coords: [
      // [null, [0, 0, 0]],
      // [[1, 1, 1]],
      // [[1, 1, 1]],
      // [[2, 2, 2]]
      // ],
      // opacity: 0.5
      // }
      // ArrayNode node = ExpressionJSONConvert.JSON_OBJECT_MAPPER.createArrayNode();
      // ObjectNode object = ExpressionJSONConvert.JSON_OBJECT_MAPPER.createObjectNode();
      // object.put("type", "cuboid");
      // node.add(object);
      // ArrayNode param = ExpressionJSONConvert.JSON_OBJECT_MAPPER.createArrayNode();
      // param.add(0.5);
      // param.add(0.5);
      // param.add(0.5);
      // object = ExpressionJSONConvert.JSON_OBJECT_MAPPER.createObjectNode();
      // object.putPOJO("color", param);
      // node.add(object);
      //
      // param = ExpressionJSONConvert.JSON_OBJECT_MAPPER.createArrayNode();
      // param.addNull();
      // ArrayNode subParam = ExpressionJSONConvert.JSON_OBJECT_MAPPER.createArrayNode();
      // subParam.add(0);
      // subParam.add(0);
      // subParam.add(0);
      // param.addPOJO(subParam);
      //
      // object = ExpressionJSONConvert.JSON_OBJECT_MAPPER.createObjectNode();
      // object.putPOJO("coords", param);
      // node.add(object);
      //
      // object = ExpressionJSONConvert.JSON_OBJECT_MAPPER.createObjectNode();
      // object.put("opacity", 0.5);
      // node.add(object);
      // return node;
      if (ast.argSize() > 0 && ast.arg1().isList()) {
        buf.append("{type: \'cuboid\',");
        setColor(buf, color, F.NIL, true);
        setOpacity(buf, opacity.orElse(F.C1D2));
        if (graphics3DCoords(buf, ast)) {
          buf.append("}");
          return true;
        }
      }
      return false;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class Cone extends AbstractEvaluator implements IGraphics3D {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        return F.Cone(F.List(F.List(0, 0, -1), F.List(0, 0, 1)), F.C1);
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
    public boolean graphics3D(StringBuilder buf, IAST ast, IAST color, IExpr opacity) {
      if (ast.argSize() > 0 && ast.arg1().isList()) {
        double radius = 1.0;
        if (ast.argSize() == 2) {
          radius = ast.arg2().toDoubleDefault(1.0);
        }
        IAST list = (IAST) ast.arg1();
        buf.append("{type: \'cone\',");
        setColor(buf, color, F.NIL, true);
        setOpacity(buf, opacity.orElse(F.C1D2));
        buf.append("radius: " + radius + ",");
        if (list.isListOfLists() && graphics3DCoords(buf, list)) {
          buf.append("}");
          return true;
        }
      }
      return false;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class Cylinder extends AbstractEvaluator implements IGraphics3D {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        return F.Cylinder(F.List(F.List(0, 0, -1), F.List(0, 0, 1)), F.C1);
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
    public boolean graphics3D(StringBuilder buf, IAST ast, IAST color, IExpr opacity) {
      if (ast.argSize() > 0 && ast.arg1().isList()) {
        double radius = 1.0;
        if (ast.argSize() == 2) {
          radius = ast.arg2().toDoubleDefault(1.0);
        }
        IAST list = (IAST) ast.arg1();
        buf.append("{type: \'cylinder\',");
        setColor(buf, color, F.NIL, true);
        setOpacity(buf, opacity.orElse(F.C1D2));
        buf.append("radius: " + radius + ",");
        if (list.isListOfLists() && graphics3DCoords(buf, list)) {
          buf.append("}");
          return true;
        }
      }
      return false;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class Dodecahedron extends Tetrahedron {

    @Override
    protected void addSubtypeThreejs(StringBuilder buf) {
      buf.append("subType: \'dodecahedron\',");
    }

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

  private static class Icosahedron extends Tetrahedron {

    @Override
    protected void addSubtypeThreejs(StringBuilder buf) {
      buf.append("subType: \'icosahedron\',");
    }

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
  private static class Initializer {

    private static void init() {
      S.Arrow.setEvaluator(new Arrow());
      S.BernsteinBasis.setEvaluator(new BernsteinBasis());
      S.Circle.setEvaluator(new Circle());
      S.Cone.setEvaluator(new Cone());
      S.Cuboid.setEvaluator(new Cuboid());
      S.Cylinder.setEvaluator(new Cylinder());
      S.Dodecahedron.setEvaluator(new Dodecahedron());
      S.Icosahedron.setEvaluator(new Icosahedron());
      S.Line.setEvaluator(new Line());
      S.Octahedron.setEvaluator(new Octahedron());
      S.Point.setEvaluator(new Point());
      S.Polygon.setEvaluator(new Polygon());
      S.Rectangle.setEvaluator(new Rectangle());
      S.Scaled.setEvaluator(new Scaled());
      S.Sphere.setEvaluator(new Sphere());
      S.Tetrahedron.setEvaluator(new Tetrahedron());
      S.Tube.setEvaluator(new Tube());
      S.Volume.setEvaluator(new Volume());
    }
  }

  private static class Line extends AbstractEvaluator implements IGraphics3D {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public boolean graphics2DDimension(IAST ast, Dimensions2D dim) {
      if (ast.arg1().isList()) {
        IAST pointList = (IAST) ast.arg1();
        double x[], y[];
        int numberOfPoints = pointList.argSize();

        double xMin = Double.MAX_VALUE;
        double xMax = Double.MIN_VALUE;
        double yMin = Double.MAX_VALUE;
        double yMax = Double.MIN_VALUE;
        x = new double[numberOfPoints];
        y = new double[numberOfPoints];
        IExpr point;
        for (int i = 0; i < numberOfPoints; i++) {
          point = pointList.get(i + 1);
          if (point.isList() && point.isAST2()) {
            x[i] = ((ISignedNumber) point.first()).doubleValue();
            if (x[i] < xMin) {
              xMin = x[i];
            }
            if (x[i] > xMax) {
              xMax = x[i];
            }
            y[i] = ((ISignedNumber) point.second()).doubleValue();
            if (y[i] < yMin) {
              yMin = y[i];
            }
            if (y[i] > yMax) {
              yMax = y[i];
            }
          }
        }
        dim.minMax(xMin, xMax, yMin, yMax);
        return true;
      }
      return false;
    }

    @Override
    public boolean graphics2D(StringBuilder buf, IAST ast, Dimensions2D dim, IAST color,
        IExpr opacity) {
      try {
        if (ast.arg1().isList()) {
          buf.append("<polyline points=\"");
          IAST pointList = (IAST) ast.arg1();
          double x[], y[];
          int numberOfPoints = pointList.argSize();

          int width = dim.width;
          int height = dim.height;
          double xMin = dim.xMin;
          double xMax = dim.xMax;
          double yMin = dim.yMin;
          double yMax = dim.yMax;
          x = new double[numberOfPoints];
          y = new double[numberOfPoints];
          IExpr point;
          for (int i = 0; i < numberOfPoints; i++) {
            point = pointList.get(i + 1);
            if (point.isList() && ((IAST) point).isAST2()) {
              x[i] = ((ISignedNumber) point.first()).doubleValue();
              y[i] = ((ISignedNumber) point.second()).doubleValue();
            }
          }
          double xAxisScalingFactor = width / (xMax - xMin);
          double yAxisScalingFactor = height / (yMax - yMin);
          for (int i = 0; i < numberOfPoints; i++) {
            buf.append(FORMATTER.format(((x[i] - xMin) * xAxisScalingFactor)));
            buf.append(",");
            buf.append(FORMATTER.format(height - ((y[i] - yMin) * yAxisScalingFactor)));
            if (i < numberOfPoints - 1) {
              buf.append(" ");
            }
          }
          return true;
        }
      } catch (RuntimeException ex) {
        // catch cast exceptions for example
        LOGGER.error("Line.graphics2D() failed", ex);
      } finally {
        buf.append(
            "\" \n style=\"stroke: rgb(0.000000%, 0.000000%, 0.000000%); stroke-opacity: 1; stroke-width: 0.666667px; fill: none\" />");
      }
      return false;
    }

    @Override
    public boolean graphics3D(StringBuilder buf, IAST ast, IAST color, IExpr opacity) {
      if (ast.argSize() > 0 && ast.arg1().isList()) {
        IAST list = (IAST) ast.arg1();
        buf.append("{type: \'line\',");
        setColor(buf, color, F.NIL, true);
        setOpacity(buf, opacity.orElse(F.C1));
        if (list.isListOfLists() && graphics3DCoords(buf, list)) {
          buf.append("}");
          return true;
        }
      }
      return false;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class Octahedron extends Tetrahedron {

    @Override
    protected void addSubtypeThreejs(StringBuilder buf) {
      buf.append("subType: \'octahedron\',");
    }

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

  private static class Point extends AbstractEvaluator implements IGraphics3D {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }

    private static void singlePointToSVG(IAST point, StringBuilder buf, Dimensions2D dim) {
      try {
        double xMin = dim.xMin;
        double yMax = dim.yMax;
        buf.append("<circle ");
        double xAxisScalingFactor = dim.getXScale();
        double yAxisScalingFactor = dim.getYScale();
        double x1 = ((ISignedNumber) point.arg1()).doubleValue();
        double y1 = ((ISignedNumber) point.arg2()).doubleValue();
        double r = 2.166667;
        // x="0.000000" y="0.000000" width="350.000000" height="350.000000"
        double cx = (x1 - xMin) * xAxisScalingFactor;
        double cy = (yMax - y1) * yAxisScalingFactor;
        buf.append("cx=\"");
        buf.append(FORMATTER.format(cx));
        buf.append("\" cy=\"");
        buf.append(FORMATTER.format(cy));
        buf.append("\" r=\"");
        buf.append(FORMATTER.format(r));
      } catch (RuntimeException ex) {
        // catch cast exceptions for example
        LOGGER.error("Point.singlePointToSVG() failed", ex);
      } finally {
        buf.append("\" \n      style=\"stroke: none; stroke-width: 0.000000px; ");
        buf.append("fill: rgb(");
        dim.getColorRGB(buf);
        buf.append("); ");
        buf.append("fill-opacity: 1\" />\n");
      }
    }

    private static void singlePointDimensions(IAST point, Dimensions2D dim) {
      double x1 = ((ISignedNumber) point.arg1()).doubleValue();
      double y1 = ((ISignedNumber) point.arg2()).doubleValue();

      dim.minMax(x1 - Config.DOUBLE_TOLERANCE, x1 + Config.DOUBLE_TOLERANCE,
          y1 - Config.DOUBLE_TOLERANCE, y1 + Config.DOUBLE_TOLERANCE);
    }

    @Override
    public boolean graphics2DDimension(IAST ast, Dimensions2D dim) {
      if (ast.size() == 2) {
        IExpr arg1 = ast.arg1();
        if (arg1.isListOfLists()) {
          IAST list = (IAST) arg1;
          for (int i = 1; i < list.size(); i++) {
            if (list.get(i).isAST(S.List, 3)) {
              IAST point = (IAST) list.get(i);
              singlePointDimensions(point, dim);
            }
          }
        } else if (arg1.isAST(S.List, 3)) {
          IAST point = (IAST) ast.arg1();

          singlePointDimensions(point, dim);
        }
      }
      return false;
    }

    @Override
    public boolean graphics2D(StringBuilder buf, IAST ast, Dimensions2D dim, IAST color,
        IExpr opacity) {
      if (ast.size() == 2) {
        IExpr arg1 = ast.arg1();
        if (arg1.isListOfLists()) {
          IAST list = (IAST) arg1;
          for (int i = 1; i < list.size(); i++) {
            if (list.get(i).isAST(S.List, 3)) {
              IAST point = (IAST) list.get(i);
              singlePointToSVG(point, buf, dim);
              return true;
            }
          }
        } else if (arg1.isAST(S.List, 3)) {
          IAST point = (IAST) arg1;
          singlePointToSVG(point, buf, dim);
          return true;
        }
      }
      return false;
    }

    @Override
    public boolean graphics3D(StringBuilder buf, IAST ast, IAST color, IExpr opacity) {
      if (ast.argSize() > 0 && ast.arg1().isList()) {
        IAST list = (IAST) ast.arg1();
        buf.append("{type: \'point\',");
        setColor(buf, color, F.RGBColor(F.C0, F.C0, F.C0), true);
        setOpacity(buf, opacity.orElse(F.C1));
        if (list.isListOfLists() && graphics3DCoords(buf, list)) {
          buf.append(",pointSize: 0.02}");
          return true;
        }
      }
      return false;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class Polygon extends AbstractEvaluator implements IGraphics3D {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }

    @Override
    public boolean graphics3D(StringBuilder buf, IAST ast, IAST color, IExpr opacity) {
      if (ast.argSize() > 0 && ast.arg1().isList()) {
        IAST list = (IAST) ast.arg1();
        buf.append("{type: \'polygon\',");
        setColor(buf, color, F.NIL, true);
        setOpacity(buf, opacity.orElse(F.C1));
        if (list.isListOfLists() && graphics3DCoords(buf, list)) {
          buf.append("}");
          return true;
        }
      }
      return false;
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

  private static class Rectangle extends AbstractEvaluator implements IGraphics3D {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }

    @Override
    public boolean graphics2DDimension(IAST ast, Dimensions2D dim) {
      if (ast.size() == 2) {
        if (ast.arg1().isList2()) {
          IAST list1 = (IAST) ast.arg1();

          double x1 = ((ISignedNumber) list1.arg1()).doubleValue();
          double y1 = ((ISignedNumber) list1.arg2()).doubleValue();
          double x2 = x1 + 1.0;
          double y2 = y1 + 1.0;

          dim.minMax(x1, x2, y1, y2);
          return true;
        }
      } else if (ast.size() == 3 && ast.arg1().isList2() && ast.arg2().isList2()) {
        IAST list1 = (IAST) ast.arg1();
        IAST list2 = (IAST) ast.arg2();

        double x1 = ((ISignedNumber) list1.arg1()).doubleValue();
        double y1 = ((ISignedNumber) list1.arg2()).doubleValue();
        double x2 = ((ISignedNumber) list2.arg1()).doubleValue();
        double y2 = ((ISignedNumber) list2.arg2()).doubleValue();

        dim.minMax(x1, x2, y1, y2);
        return true;
      }
      return false;
    }

    @Override
    public boolean graphics2D(StringBuilder buf, IAST ast, Dimensions2D dim, IAST color,
        IExpr opacity) {
      try {
        int width = dim.width;
        int height = dim.height;
        double xMin = dim.xMin;
        double xMax = dim.xMax;
        double yMin = dim.yMin;
        double yMax = dim.yMax;
        if (ast.size() == 2) {
          if (ast.arg1().isAST(S.List, 3)) {
            IAST list1 = (IAST) ast.arg1();
            buf.append("<rect ");
            double xAxisScalingFactor = width / (xMax - xMin);
            double yAxisScalingFactor = height / (yMax - yMin);

            double x1 = ((ISignedNumber) list1.arg1()).doubleValue();
            double y1 = ((ISignedNumber) list1.arg2()).doubleValue();
            double w = 1.0;
            double h = 1.0;
            // x="0.000000" y="0.000000" width="350.000000" height="350.000000"
            buf.append("x=\"");
            buf.append(FORMATTER.format((x1 - xMin) * xAxisScalingFactor));
            buf.append("\" y=\"");
            buf.append(FORMATTER.format((yMax - y1 - 1) * yAxisScalingFactor));
            buf.append("\" width=\"");
            buf.append(FORMATTER.format(w * xAxisScalingFactor));
            buf.append("\" height=\"");
            buf.append(FORMATTER.format(h * yAxisScalingFactor));
            return true;
          }
        } else if (ast.size() == 3 && ast.arg1().isAST(S.List, 3) && ast.arg2().isAST(S.List, 3)) {
          IAST list1 = (IAST) ast.arg1();
          IAST list2 = (IAST) ast.arg2();
          buf.append("<rect ");
          double xAxisScalingFactor = width / (xMax - xMin);
          double yAxisScalingFactor = height / (yMax - yMin);

          double x1 = ((ISignedNumber) list1.arg1()).doubleValue();
          double y1 = ((ISignedNumber) list1.arg2()).doubleValue();
          double x2 = ((ISignedNumber) list2.arg1()).doubleValue();
          double y2 = ((ISignedNumber) list2.arg2()).doubleValue();
          double w = x2 - x1;
          double h = y2 - y1;
          buf.append("x=\"");
          buf.append(FORMATTER.format((x1 - xMin) * xAxisScalingFactor));
          buf.append("\" y=\"");
          buf.append(FORMATTER.format((yMax - y1 - h) * yAxisScalingFactor));
          buf.append("\" width=\"");
          buf.append(FORMATTER.format(w * xAxisScalingFactor));
          buf.append("\" height=\"");
          buf.append(FORMATTER.format(h * yAxisScalingFactor));
          return true;
        }

      } catch (RuntimeException ex) {
        // catch cast exceptions for example
        LOGGER.error("Rectangle.graphics2D() failed", ex);
      } finally {
        buf.append("\" \n      style=\"stroke: none; stroke-width: 0.000000px; ");
        buf.append("fill: rgb(");
        dim.getColorRGB(buf);
        buf.append("); ");
        buf.append("stroke-opacity: 1; stroke-width: 0.666667px; fill-opacity: 1\" />\n");
      }
      return false;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class Sphere extends AbstractEvaluator implements IGraphics3D {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        return F.Sphere(F.List(0, 0, 0), F.C1);
      }
      if (ast.isAST1()) {
        return F.Sphere(ast.arg1(), F.C1);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }

    private boolean sphere(StringBuilder buf, IAST sphereCoords, double sphereRadius, IAST color,
        IExpr opacity) {
      buf.append("{type: \'sphere\',");
      setColor(buf, color, color, true);
      setOpacity(buf, opacity.orElse(F.C1D2));
      buf.append("radius: " + sphereRadius + ",");
      if (sphereCoords.isList3() && graphics3DCoords(buf, F.List(sphereCoords))) {
        buf.append("}");
        return true;
      }
      return false;
    }

    @Override
    public boolean graphics3D(StringBuilder buf, IAST ast, IAST color, IExpr opacity) {
      if (ast.argSize() > 0 && ast.arg1().isList()) {
        double radius = 1.0;
        if (ast.argSize() == 2) {
          radius = ast.arg2().toDoubleDefault(1.0);
        }
        IAST list = (IAST) ast.arg1();
        if (list.isListOfLists()) {
          for (int i = 1; i < list.size(); i++) {
            IExpr arg = list.get(i);
            if (!arg.isList3()) {
              return false;
            }
            if (!sphere(buf, (IAST) arg, radius, color, opacity)) {
              return false;
            }
            if (i < list.size() - 1) {
              buf.append(",");
            }
          }
          return true;
        }
        return sphere(buf, list, radius, color, opacity);
      }
      return false;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class Tetrahedron extends AbstractEvaluator implements IGraphics3D {

    protected void addSubtypeThreejs(StringBuilder buf) {
      buf.append("subType: \'tetrahedron\',");
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_INFINITY;
    }

    @Override
    public boolean graphics3D(StringBuilder buf, IAST ast, IAST color, IExpr opacity) {

      IAST list = F.List(F.List(F.C0, F.C0, F.C0));
      if (ast.argSize() > 0 && ast.arg1().isList()) {
        list = (IAST) ast.arg1();
      }
      buf.append("{type: \'uniformPolyhedron\',");
      setColor(buf, color, F.NIL, true);
      setOpacity(buf, opacity.orElse(F.C1D2));
      addSubtypeThreejs(buf);
      if (list.isListOfLists() && graphics3DCoords(buf, list)) {
        buf.append("}");
        return true;
      }

      return false;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class Tube extends AbstractEvaluator implements IGraphics3D {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public boolean graphics3D(StringBuilder buf, IAST ast, IAST color, IExpr opacity) {
      if (ast.argSize() > 0 && ast.arg1().isList()) {
        double radius = 0.01;
        if (ast.argSize() == 2) {
          radius = ast.arg2().toDoubleDefault(1.0);
        }
        IAST list = (IAST) ast.arg1();
        buf.append("{type: \'tube\',");
        setColor(buf, color, F.NIL, true);
        setOpacity(buf, opacity.orElse(F.C1));
        buf.append("radius: " + radius + ",");
        if (list.isListOfLists() && graphics3DCoords(buf, list)) {
          buf.append("}");
          return true;
        }
      }
      return false;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class Volume extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isAST()) {
        IAST graphic = (IAST) ast.arg1();
        if (graphic.isAST(S.Ball, 2, 3) && graphic.first().isList3()) {
          IExpr radius = F.C1;
          if (graphic.size() == 3) {
            radius = graphic.second();
          }
          return F.Times(F.C3D4, S.Pi, F.Power(radius, F.C3));
        } else if (graphic.isAST(S.Cuboid, 3) && graphic.first().isList3()
            && graphic.second().isList3()) {
          IAST v1 = (IAST) graphic.first();
          IAST v2 = (IAST) graphic.second();
          // Abs((-a + x)*(-b + y)*(-c + z))
          return F.Abs(F.Times( //
              F.Plus(v1.arg1().negate(), v2.arg1()), F.Plus(v1.arg2().negate(), v2.arg2()),
              F.Plus(v1.arg3().negate(), v2.arg3())));
        } else if (graphic.isAST(S.Ellipsoid, 3) && graphic.first().isList3()
            && graphic.second().isList3()) {
          // IAST v1 = (IAST) graphic.first();
          IAST v2 = (IAST) graphic.second();
          return F.Times(F.QQ(4, 3), S.Pi, v2.arg1(), v2.arg2(), v2.arg3());
        }
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  public static boolean exportGraphicsSVG(StringBuilder buf, IAST data2D, Dimensions2D dim) {
    if (data2D.isList()) {
      boolean first = true;
      IAST rgbColor = F.NIL;
      IExpr opacity = F.num(0.75);

      for (int i = 1; i < data2D.size(); i++) {
        IExpr arg = data2D.get(i);
        if (arg.isAST()) {
          IAST primitive = (IAST) arg;
          if (primitive.isAST(S.RGBColor, 4)) {
            rgbColor = primitive;
            continue;
          }
          if (primitive.isAST(S.Opacity, 2)) {
            opacity = primitive.arg1();
            continue;
          }

          if (primitive.head().isBuiltInSymbol()) {
            IBuiltInSymbol symbol = (IBuiltInSymbol) primitive.head();
            IEvaluator evaluator = symbol.getEvaluator();
            if (evaluator instanceof IGraphics3D) {
              if (!first) {
                buf.append(",");
              }
              first = false;

              if (!((IGraphics3D) evaluator).graphics2D(buf, primitive, dim, rgbColor, opacity)) {
                return false;
              }
              continue;
            }
          }
        }
      }
      return true;
    }
    return false;
  }

  public static boolean exportGraphics3DRecursive(StringBuilder buf, IAST data3D) {
    if (data3D.isList()) {
      boolean first = true;
      IAST rgbColor = F.NIL;
      IExpr opacity = F.NIL;
      IAST list = data3D;
      for (int i = 1; i < list.size(); i++) {
        IExpr arg = list.get(i);
        if (arg.isAST()) {
          IAST ast = (IAST) arg;
          if (ast.isList()) {
            StringBuilder primitivesBuffer = new StringBuilder();
            if (exportGraphics3DRecursive(primitivesBuffer, ast)) {
              if (!first) {
                buf.append(",");
              }
              first = false;
              buf.append(primitivesBuffer);
            }
          } else if (ast.isRGBColor()) {
            rgbColor = ast;
          } else if (ast.isAST(S.Opacity, 2)) {
            opacity = ast.arg1();
          } else if (ast.head().isBuiltInSymbol()) {
            IBuiltInSymbol symbol = (IBuiltInSymbol) ast.head();
            IEvaluator evaluator = symbol.getEvaluator();
            if (evaluator instanceof IGraphics3D) {
              StringBuilder primitivesBuffer = new StringBuilder();
              if (((IGraphics3D) evaluator).graphics3D(primitivesBuffer, ast, rgbColor, opacity)) {
                if (!first) {
                  buf.append(",");
                }
                first = false;
                buf.append(primitivesBuffer);
              }
            }
          }
        }
      }
      return true;
    }
    return false;
  }

  private static boolean graphics3DCoords(StringBuilder buf, IAST ast) {
    return graphics3DCoords(buf, ast, "coords");
  }

  private static boolean graphics3DCoords(StringBuilder buf, IAST ast, String coordStr) {
    buf.append(coordStr + ": [");

    for (int i = 1; i < ast.size(); i++) {
      IExpr arg = ast.get(i);
      if (!arg.isList3()) {
        return false;
      }
      IAST coords = (IAST) arg;
      buf.append("[[");
      coords.joinToString(buf, ",");
      buf.append("]]");
      if (i < ast.size() - 1) {
        buf.append(",");
      }
    }
    buf.append("]");
    return true;
  }

  private static boolean graphics3DCoordsOrListOfCoords(StringBuilder buf,
      IAST coordsOrListOfCoords, String coordStr) {
    buf.append(coordStr + ": ");
    buf.append("[");
    if (coordsOrListOfCoords.isListOfLists()) {
      final int size = coordsOrListOfCoords.size();
      for (int i = 1; i < size; i++) {
        buf.append("[");
        IAST subList = (IAST) coordsOrListOfCoords.get(i);
        subList.joinToString(buf, ",");
        buf.append("]");
        if (i < size - 1) {
          buf.append(",");
        }
      }
    } else {
      buf.append("[");
      coordsOrListOfCoords.joinToString(buf, ",");
      buf.append("]");
    }
    buf.append("]");
    return true;
  }

  public static void initialize() {
    Initializer.init();
  }

  public static boolean renderGraphics3D(StringBuilder graphics3DBuffer, IAST graphics3DAST,
      EvalEngine engine) {
    IExpr arg1 = graphics3DAST.first();
    if (!arg1.isList()) {
      arg1 = F.List(arg1);
    }
    IExpr lighting = S.Automatic; // .List(F.$str("Auto"), F.RGBColor(F.C1, F.C1, F.C1));
    OptionArgs options = OptionArgs.createOptionArgs(graphics3DAST, engine);
    if (options != null) {
      lighting = options.getOption(S.Lighting).orElse(lighting);

      // if (option.isList1() && option.first().isList() && option.first().first().isString()) {
      // lighting = option.first();
      // }
    }
    IExpr data3D = engine.evaluate(F.N(arg1));
    if (data3D.isAST() && data3D.head().isBuiltInSymbol()) {
      StringBuilder jsonPrimitives = new StringBuilder();
      if (GraphicsFunctions.exportGraphics3DRecursive(jsonPrimitives, (IAST) data3D)) {
        try {

          graphics3DBuffer.append("drawGraphics3d(document.getElementById('graphics3d'),\n");
          graphics3DBuffer.append("{");
          graphics3DBuffer.append("\naxes: {},");
          graphics3DBuffer.append("\nelements: [");
          graphics3DBuffer.append(jsonPrimitives.toString());
          graphics3DBuffer.append("],");
          graphics3DLigthing(graphics3DBuffer, lighting);
          graphics3DBuffer.append("\nviewpoint: [1.3, -2.4, 2.0]");
          graphics3DBuffer.append("}\n");
          graphics3DBuffer.append(");");
          return true;
        } catch (Exception ex) {
          LOGGER.debug("GraphicsFunctions.renderGraphics3D() failed", ex);
        }
      }
    }
    return false;
  }

  private static void graphics3DLigthing(StringBuilder graphics3DBuffer, IExpr lighting) {
    IAST automatic = F.List(F.AmbientLight(F.RGBColor(0.4, 0.2, 0.2)),
        F.DirectionalLight(F.RGBColor(0., 0.18, 0.5), F.List(2, 0, 2)),
        F.DirectionalLight(F.RGBColor(0.18, 0.5, 0.18), F.List(2, 2, 3)),
        F.DirectionalLight(F.RGBColor(0.5, 0.18, 0.), F.List(0, 2, 2)),
        F.DirectionalLight(F.RGBColor(0., 0., 0.18), F.List(0, 0, 2)));
    IAST result = F.NIL;
    if (lighting.equals(S.Automatic)) {
      result = automatic;
    } else if (lighting.equals(F.$str("Neutral"))) {
      result = F.List(F.AmbientLight(F.RGBColor(0.35, 0.35, 0.35)),
          F.DirectionalLight(F.RGBColor(0.37, 0.37, 0.37), F.List(2, 0, 2)),
          F.DirectionalLight(F.RGBColor(0.37, 0.37, 0.37), F.List(2, 2, 3)),
          F.DirectionalLight(F.RGBColor(0.37, 0.37, 0.37), F.List(0, 2, 2)));
    } else if (lighting.isAST()) {
      result = (IAST) lighting;
    }

    boolean lightingDone = false;
    graphics3DBuffer.append("\nlighting: [");
    if (result.isPresent()) {
      if (result.isList()) {
        for (int i = 1; i < result.size(); i++) {
          if (result.get(i).isAST()) {
            if (lightingDone) {
              graphics3DBuffer.append(",");
            }
            if (graphics3DSingleLight(graphics3DBuffer, (IAST) result.get(i))) {

              lightingDone = true;
            }
          }
        }
      } else {
        lightingDone = graphics3DSingleLight(graphics3DBuffer, result);
      }
    }

    if (!lightingDone) {
      lightingDone = graphics3DSingleLight(graphics3DBuffer, automatic);
    }
    graphics3DBuffer.append("\n],");
  }

  private static boolean graphics3DSingleLight(StringBuilder graphics3DBuffer, IAST result) {
    if (result.isAST1()) {
      IExpr color = result.arg1();
      if (color.isRGBColor()) {
        if (result.head().equals(S.AmbientLight)) {
          graphics3DBuffer.append("\n{");
          graphics3DBuffer.append("type: 'ambient',");
          setColor(graphics3DBuffer, (IAST) color, F.RGBColor(F.C1, F.C1, F.C1), false);
          graphics3DBuffer.append("}");
          return true;
        }
      }
    } else if (result.isAST2()) {
      String name = result.arg1().toString();
      if (name.equals("Ambient")) {
        IExpr color = result.arg2();
        if (color.isRGBColor()) {
          graphics3DBuffer.append("\n{");
          graphics3DBuffer.append("type: 'ambient',");
          setColor(graphics3DBuffer, (IAST) color, F.NIL, false);
          graphics3DBuffer.append("}");
          return true;
        }
      } else {
        IExpr color = result.arg1();
        if (color.isRGBColor() && result.arg2().isList()) {
          IAST list = (IAST) result.arg2();
          if (result.head().equals(S.DirectionalLight) && list.isList()) {
            graphics3DBuffer.append("\n{");
            graphics3DBuffer.append("type: 'directional',");
            setColor(graphics3DBuffer, (IAST) color, F.NIL, true);
            graphics3DCoordsOrListOfCoords(graphics3DBuffer, list, "coords");
            graphics3DBuffer.append("}");
            return true;
          } else if (result.head().equals(S.PointLight) && list.isList3()) {
            graphics3DBuffer.append("\n{");
            graphics3DBuffer.append("type: 'point',");
            setColor(graphics3DBuffer, (IAST) color, F.NIL, true);
            graphics3DCoordsOrListOfCoords(graphics3DBuffer, list, "coords");
            graphics3DBuffer.append("}");
            return true;
          } else if (result.head().equals(S.SpotLight) && list.isList2() && list.isListOfLists()) {
            IAST coords = (IAST) list.arg1();
            IAST target = (IAST) list.arg2();
            if (coords.isList3() && target.isList3()) {
              double angle = 1.0;
              if (result.size() == 5) {
                angle = result.arg4().toDoubleDefault(1.0);
              }
              graphics3DBuffer.append("\n{");
              graphics3DBuffer.append("type: 'spot',");
              graphics3DBuffer.append("angle: " + angle + ",");
              setColor(graphics3DBuffer, (IAST) color, F.NIL, true);
              graphics3DCoordsOrListOfCoords(graphics3DBuffer, coords, "coords");
              graphics3DBuffer.append(",");
              graphics3DCoordsOrListOfCoords(graphics3DBuffer, target, "target");
              graphics3DBuffer.append("}");
              return true;
            }
          }
        }
      }
    } else if (result.isAST(S.List, 4, 5) && result.arg3().isList() && result.size() > 2) {
      String name = result.arg1().toString();

      IExpr color = result.arg2();
      IAST list = (IAST) result.arg3();
      if (color.isRGBColor()) {
        if (name.equals("Directional") && list.isList()) {
          graphics3DBuffer.append("\n{");
          graphics3DBuffer.append("type: 'directional',");
          setColor(graphics3DBuffer, (IAST) color, F.NIL, true);
          graphics3DCoordsOrListOfCoords(graphics3DBuffer, list, "coords");
          graphics3DBuffer.append("}");
          return true;
        } else if (name.equals("Point") && list.isList3()) {
          graphics3DBuffer.append("\n{");
          graphics3DBuffer.append("type: 'point',");
          setColor(graphics3DBuffer, (IAST) color, F.NIL, true);
          graphics3DCoordsOrListOfCoords(graphics3DBuffer, list, "coords");
          graphics3DBuffer.append("}");
          return true;
        } else if (name.equals("Spot") && list.isList2() && list.isListOfLists()) {
          IAST coords = (IAST) list.arg1();
          IAST target = (IAST) list.arg2();
          if (coords.isList3() && target.isList3()) {
            double angle = 1.0;
            if (result.size() == 5) {
              angle = result.arg4().toDoubleDefault(1.0);
            }
            graphics3DBuffer.append("\n{");
            graphics3DBuffer.append("type: 'spot',");
            graphics3DBuffer.append("angle: " + angle + ",");
            setColor(graphics3DBuffer, (IAST) color, F.NIL, true);
            graphics3DCoordsOrListOfCoords(graphics3DBuffer, coords, "coords");
            graphics3DBuffer.append(",");
            graphics3DCoordsOrListOfCoords(graphics3DBuffer, target, "target");
            graphics3DBuffer.append("}");
            return true;
          }
        }
      }
    }
    return false;

  }

  private static void setColor(StringBuilder buf, IAST color, IAST defaultColor, boolean setComma) {
    if (color.isPresent()) {
      if (color.isAST(S.RGBColor, 4, 5)) {
        if (color.size() == 5) {
          double opacity = color.arg4().toDoubleDefault(1.0);
          buf.append("opacity: ");
          buf.append(opacity);
          buf.append(",");
        }
        double red = color.arg1().toDoubleDefault(0.0);
        double green = color.arg2().toDoubleDefault(0.0);
        double blue = color.arg3().toDoubleDefault(0.0);
        buf.append("color: [");
        buf.append(red);
        buf.append(",");
        buf.append(green);
        buf.append(",");
        buf.append(blue);
        buf.append("]");
        if (setComma) {
          buf.append(",");
        }
        return;
      } else if (color.isAST(S.RGBColor, 1) && color.arg1().isAST(S.List, 4, 5)) {
        IAST list = (IAST) color.arg1();
        if (color.size() == 5) {
          double opacity = list.arg4().toDoubleDefault(1.0);
          buf.append("opacity: ");
          buf.append(opacity);
          buf.append(",");
        }
        double red = list.arg1().toDoubleDefault(0.0);
        double green = list.arg2().toDoubleDefault(0.0);
        double blue = list.arg3().toDoubleDefault(0.0);
        buf.append("color: [");
        buf.append(red);
        buf.append(",");
        buf.append(green);
        buf.append(",");
        buf.append(blue);
        buf.append("]");
        if (setComma) {
          buf.append(",");
        }
        return;
      }
    }
    if (defaultColor.isAST(S.RGBColor, 4)) {
      double red = defaultColor.arg1().toDoubleDefault(0.0);
      double green = defaultColor.arg2().toDoubleDefault(0.0);
      double blue = defaultColor.arg3().toDoubleDefault(0.0);
      buf.append("color: [");
      buf.append(red);
      buf.append(",");
      buf.append(green);
      buf.append(",");
      buf.append(blue);
      buf.append("]");
    } else {
      buf.append("color: [1.0, 0.5, 0.0]");
    }

    if (setComma) {
      buf.append(",");
    }
  }

  private static void setOpacity(StringBuilder buf, IExpr opacityExpr) {
    double opacity = opacityExpr.toDoubleDefault(1.0);
    buf.append("opacity: ");
    buf.append(opacity);
    buf.append(",");
  }

  private GraphicsFunctions() {}

  public static boolean primitivesDimension(IAST list, Dimensions2D dim) {
    for (int i = 1; i < list.size(); i++) {
      if (list.get(i).isAST()) {
        IAST primitive = (IAST) list.get(i);
        if (primitive.head().isBuiltInSymbol()) {
          IBuiltInSymbol symbol = (IBuiltInSymbol) primitive.head();
          IEvaluator evaluator = symbol.getEvaluator();
          if (evaluator instanceof IGraphics3D) {
            if (!((IGraphics3D) evaluator).graphics2DDimension(primitive, dim)) {
              // return false;
            }
          }
        }
      }
    }
    return true;
  }

  public static void graphicsToSVG(IAST ast, StringBuilder buf) {
    EvalEngine engine = EvalEngine.get();
    IAST numericAST = (IAST) engine.evalN(ast);
    Dimensions2D dim = new Dimensions2D(350, 350);
    // set a default value
    dim.color = RGBColor.BLUE;
    if (numericAST.size() > 2) {
      final OptionArgs options = new OptionArgs(numericAST.topHead(), numericAST, 2, engine);
      IExpr option = options.getOption(S.PlotRange);
      if (option.isListOfLists() && option.size() == 3) {
        IAST list = (IAST) option;
        dim.setPlotRange(list.getAST(1), list.getAST(2));
      }
      option = options.getOption(S.Axes);
      if (option.isTrue()) {
        dim.setAxes(true);
      }
    }

    try {
      int width = dim.width;
      int height = dim.height;

      if (ast.size() > 1) {
        IExpr arg1 = ast.arg1();
        if (!arg1.isList()) {
          arg1 = F.List(arg1);
        }
        primitivesDimension((IAST) arg1, dim);

        exportGraphicsSVG(buf, (IAST) arg1, dim);
      }

      if (dim.isAxes()) {
        double xScale = width / (dim.xMax - dim.xMin);
        double yScale = height / (dim.yMax - dim.yMin);
        double x1 = 0;

        // vertical axe
        // + "0.000000,233.333333 6.666667,233.333333");
        buf.append("<polyline points=\"");

        buf.append(Show2SVG.FORMATTER.format((x1 - dim.xMin) * xScale));
        buf.append(",");
        buf.append(Show2SVG.FORMATTER.format(0.0));
        buf.append(" ");
        buf.append(Show2SVG.FORMATTER.format((x1 - dim.xMin) * xScale));
        buf.append(",");
        buf.append(Show2SVG.FORMATTER.format(height));

        buf.append(
            "\" style=\"stroke: rgb(0.000000%, 0.000000%, 0.000000%); stroke-opacity: 1; stroke-width: 0.666667px; fill: none\"/>\n");

        // horizontals axe
        double y1 = (-dim.yMin) * yScale;
        buf.append("<polyline points=\"");

        buf.append(Show2SVG.FORMATTER.format(0));
        buf.append(",");
        buf.append(Show2SVG.FORMATTER.format(y1));
        buf.append(" ");
        buf.append(Show2SVG.FORMATTER.format(width));
        buf.append(",");
        buf.append(Show2SVG.FORMATTER.format(y1));

        buf.append(
            "\" style=\"stroke: rgb(0.000000%, 0.000000%, 0.000000%); stroke-opacity: 1; stroke-width: 0.666667px; fill: none\"/>\n");
      }
    } finally {
    }
  }

  /**
   * @param functionColorNumber the number of the color the function should be plotted in
   * @param plotStyle if present a <code>List()</code> is expected
   */
  public static RGBColor plotStyleColor(int functionColorNumber, IAST plotStyle) {
    if (plotStyle.isList() && plotStyle.size() > functionColorNumber) {
      IExpr temp = plotStyle.get(functionColorNumber);
      if (temp.isASTSizeGE(S.Directive, 2)) {
        IAST directive = (IAST) temp;
        for (int j = 1; j < directive.size(); j++) {
          temp = directive.get(j);
          RGBColor color = Convert.toAWTColor(temp);
          if (color != null) {
            return color;
          }
        }
      } else {
        RGBColor color = Convert.toAWTColor(temp);
        if (color != null) {
          return color;
        }
      }
    }
    return PLOT_COLORS[(functionColorNumber - 1) % PLOT_COLORS.length];
  }

  /**
   * Get an {@link F#RGBColor(double, double, double)} color for the function number from the
   * internal color wheel.
   *
   * @param functionColorNumber the number of the function which should be plotted
   * @param plotStyle if present a <code>List()</code> is expected
   * @return
   */
  public static IAST plotStyleColorExpr(int functionColorNumber, IAST plotStyle) {
    RGBColor color = plotStyleColor(functionColorNumber, plotStyle);
    float[] rgbComponents = color.getRGBColorComponents(null);
    return F.RGBColor(rgbComponents[0], rgbComponents[1], rgbComponents[2]);
  }
}
