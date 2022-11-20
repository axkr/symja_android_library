package org.matheclipse.core.builtin;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.convert.ExpressionJSONConvert;
import org.matheclipse.core.convert.RGBColor;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.Dimensions2D;
import org.matheclipse.core.graphics.IGraphics2D;
import org.matheclipse.core.graphics.IGraphics3D;
import org.matheclipse.core.graphics.Show2SVG;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class GraphicsFunctions {
  private static final Logger LOGGER = LogManager.getLogger();

  /**
   * From the docs: "Mapper instances are fully thread-safe provided that ALL configuration of the
   * instance occurs before ANY read or write calls."
   */
  public static final ObjectMapper JSON_OBJECT_MAPPER = new ObjectMapper();

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

  private static class Arrow extends AbstractEvaluator implements IGraphics2D, IGraphics3D {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }

    @Override
    public boolean graphics2D(ObjectNode json, IAST ast, IAST color, IExpr opacity,
        IASTAppendable listOfCoords) {
      if (ast.argSize() > 0 && ast.arg1().isList()) {
        IAST list = (IAST) ast.arg1();
        json.put("type", "arrow");
        setColor(json, color, F.NIL);
        setOpacity(json, opacity.orElse(F.C1));
        if (list.isListOfLists() && graphics2DCoords(json, list, listOfCoords)) {
          return true;
        }
      }
      return false;
    }

    @Override
    public boolean graphics3D(ObjectNode json, IAST ast, IAST color, IExpr opacity) {
      if (ast.argSize() > 0 && ast.arg1().isList()) {
        IAST list = (IAST) ast.arg1();
        json.put("type", "arrow");
        setColor(json, color, F.NIL);
        setOpacity(json, opacity.orElse(F.C1));
        if (list.isListOfLists() && graphics3DCoords(json, list)) {
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

  private static class Circle extends AbstractEvaluator implements IGraphics2D, IGraphics3D {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.argSize() == 0) {
        return F.Circle(F.List(F.C0, F.C0));
      }
      return F.NIL;
    }

    protected String getJSONType() {
      return "circle";
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_3;
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

    private static boolean circle(ObjectNode json, String jsonType, IAST circleCoords,
        double circleRadius1, double circleRadius2, double angle1, double angle2, IAST color,
        IExpr opacity, IASTAppendable listOfCoords) {
      json.put("type", jsonType);
      setColor(json, color, color);
      setOpacity(json, opacity.orElse(F.C1D2));
      json.put("radius1", circleRadius1);
      json.put("radius2", circleRadius2);
      if (angle1 != 0.0 || angle2 != Math.PI * 2) {
        // not a full circle
        json.put("angle1", angle1);
        json.put("angle2", angle2);
      }
      if (circleCoords.isList2() && graphics2DCoords(json, F.list(circleCoords), listOfCoords,
          circleRadius1, circleRadius2)) {
        return true;
      }
      return false;
    }

    @Override
    public boolean graphics2D(ObjectNode json, IAST ast, IAST color, IExpr opacity,
        IASTAppendable listOfCoords) {
      if (ast.argSize() > 0 && ast.arg1().isList2()) {
        double radius1 = 1.0;
        double radius2 = 1.0;
        double angle1 = 0.0;
        double angle2 = Math.PI * 2.0;
        if (ast.argSize() >= 2) {
          IExpr arg2 = ast.arg2();
          if (arg2.isList2()) {
            // ellipsis
            IAST pair = (IAST) arg2;
            radius1 = pair.first().toDoubleDefault(1.0);
            radius2 = pair.second().toDoubleDefault(1.0);
          } else {
            // circle radius
            radius1 = arg2.toDoubleDefault(1.0);
            radius2 = radius1;
          }
        }
        if (ast.argSize() == 3 && ast.arg3().isList2()) {
          // describe an arc with 2 angles
          IAST pair = (IAST) ast.arg3();
          angle1 = pair.first().toDoubleDefault(1.0);
          angle2 = pair.second().toDoubleDefault(1.0);
        }
        IAST list = (IAST) ast.arg1();
        if (list.isListOfLists()) {
          for (int i = 1; i < list.size(); i++) {
            IExpr arg = list.get(i);
            if (!arg.isList2()) {
              return false;
            }
            if (!circle(json, getJSONType(), (IAST) arg, radius1, radius2, angle1, angle2, color,
                opacity, listOfCoords)) {
              return false;
            }
          }
          return true;
        }
        if (circle(json, getJSONType(), list, radius1, radius2, angle1, angle2, color, opacity,
            listOfCoords)) {
          return true;
        }
      }
      return false;
    }

    @Override
    public boolean graphics2DSVG(StringBuilder buf, IAST ast, Dimensions2D dim, IAST color,
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
    public boolean graphics3D(ObjectNode json, IAST ast, IAST color, IExpr opacity) {
      if (ast.argSize() > 0 && ast.arg1().isList()) {
        json.put("type", "cuboid");
        setColor(json, color, F.NIL);
        setOpacity(json, opacity.orElse(F.C1D2));
        if (graphics3DCoords(json, ast)) {
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
    public boolean graphics3D(ObjectNode json, IAST ast, IAST color, IExpr opacity) {
      if (ast.argSize() > 0 && ast.arg1().isList()) {
        double radius = 1.0;
        if (ast.argSize() == 2) {
          radius = ast.arg2().toDoubleDefault(1.0);
        }
        IAST list = (IAST) ast.arg1();
        json.put("type", "cone");
        setColor(json, color, F.NIL);
        setOpacity(json, opacity.orElse(F.C1D2));
        json.put("radius", radius);
        if (list.isListOfLists() && graphics3DCoords(json, list)) {
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
    public boolean graphics3D(ObjectNode json, IAST ast, IAST color, IExpr opacity) {
      if (ast.argSize() > 0 && ast.arg1().isList()) {
        double radius = 1.0;
        if (ast.argSize() == 2) {
          radius = ast.arg2().toDoubleDefault(1.0);
        }
        IAST list = (IAST) ast.arg1();
        json.put("type", "cylinder");
        setColor(json, color, F.NIL);
        setOpacity(json, opacity.orElse(F.C1D2));
        json.put("radius", radius);
        if (list.isListOfLists() && graphics3DCoords(json, list)) {
          return true;
        }
      }
      return false;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
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
    protected void addSubtypeThreejs(ObjectNode json) {
      json.put("subType", "dodecahedron");
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

  private static class GraphicsJSON extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isAST(S.Graphics)) {
        StringBuilder graphics2DBuffer = new StringBuilder();
        if (renderGraphics2D(graphics2DBuffer, (IAST) arg1, false, engine)) {
          return F.stringx(graphics2DBuffer.toString(), IStringX.TEXT_JSON);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

  }

  private static class Graphics3DJSON extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isAST(S.Graphics3D)) {
        StringBuilder graphics3DBuffer = new StringBuilder();
        if (renderGraphics3D(graphics3DBuffer, (IAST) arg1, false, engine)) {
          return F.stringx(graphics3DBuffer.toString(), IStringX.TEXT_JSON);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

  }

  private static class Icosahedron extends Tetrahedron {

    @Override
    protected void addSubtypeThreejs(ObjectNode json) {
      json.put("subType", "icosahedron");
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
      S.Disk.setEvaluator(new Disk());
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
      S.Text.setEvaluator(new Text());
      S.Tube.setEvaluator(new Tube());
      S.Volume.setEvaluator(new Volume());
      S.GraphicsJSON.setEvaluator(new GraphicsJSON());
      S.Graphics3DJSON.setEvaluator(new Graphics3DJSON());
    }
  }

  private static class Line extends AbstractEvaluator implements IGraphics2D, IGraphics3D {

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
    public boolean graphics2DSVG(StringBuilder buf, IAST ast, Dimensions2D dim, IAST color,
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
    public boolean graphics2D(ObjectNode json, IAST ast, IAST color, IExpr opacity,
        IASTAppendable listOfCoords) {
      if (ast.argSize() > 0 && ast.arg1().isList()) {
        IAST list = (IAST) ast.arg1();
        json.put("type", "line");
        setColor(json, color, F.NIL);
        setOpacity(json, opacity.orElse(F.C1));
        if (list.isListOfLists() && graphics2DCoords(json, list, listOfCoords)) {
          return true;
        }
      }
      return false;
    }

    @Override
    public boolean graphics3D(ObjectNode json, IAST ast, IAST color, IExpr opacity) {
      if (ast.argSize() > 0 && ast.arg1().isList()) {
        IAST list = (IAST) ast.arg1();
        json.put("type", "line");
        setColor(json, color, F.NIL);
        setOpacity(json, opacity.orElse(F.C1));
        if (list.isListOfLists() && graphics3DCoords(json, list)) {
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
    protected void addSubtypeThreejs(ObjectNode json) {
      json.put("subType", "octahedron");
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

  private static class Point extends AbstractEvaluator implements IGraphics2D, IGraphics3D {

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
    public boolean graphics2DSVG(StringBuilder buf, IAST ast, Dimensions2D dim, IAST color,
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
    public boolean graphics2D(ObjectNode json, IAST ast, IAST color, IExpr opacity,
        IASTAppendable listOfCoords) {
      if (ast.argSize() > 0 && ast.arg1().isList()) {
        IAST list = (IAST) ast.arg1();
        json.put("type", "point");
        setColor(json, color, F.RGBColor(F.C0, F.C0, F.C0));
        setOpacity(json, opacity.orElse(F.C1));
        if (list.isListOfLists() && graphics2DCoords(json, list, listOfCoords)) {
          return true;
        }
      }
      return false;
    }

    @Override
    public boolean graphics3D(ObjectNode json, IAST ast, IAST color, IExpr opacity) {
      if (ast.argSize() > 0 && ast.arg1().isList()) {
        IAST list = (IAST) ast.arg1();
        json.put("type", "point");
        setColor(json, color, F.RGBColor(F.C0, F.C0, F.C0));
        setOpacity(json, opacity.orElse(F.C1));
        if (list.isListOfLists() && graphics3DCoords(json, list)) {
          json.put("pointSize", 0.02);
          return true;
        }
      }
      return false;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class Polygon extends AbstractEvaluator implements IGraphics2D, IGraphics3D {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }

    @Override
    public boolean graphics2D(ObjectNode json, IAST ast, IAST color, IExpr opacity,
        IASTAppendable listOfCoords) {
      if (ast.argSize() > 0 && ast.arg1().isList()) {
        IAST list = (IAST) ast.arg1();
        json.put("type", "polygon");
        setColor(json, color, F.NIL);
        setOpacity(json, opacity.orElse(F.C1));
        if (list.isListOfLists() && graphics2DCoords(json, list, listOfCoords)) {
          return true;
        }
      }
      return false;
    }

    @Override
    public boolean graphics3D(ObjectNode json, IAST ast, IAST color, IExpr opacity) {
      if (ast.argSize() > 0 && ast.arg1().isList()) {
        IAST list = (IAST) ast.arg1();
        json.put("type", "polygon");
        setColor(json, color, F.NIL);
        setOpacity(json, opacity.orElse(F.C1));
        if (list.isListOfLists() && graphics3DCoords(json, list)) {
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

  private static class Rectangle extends AbstractEvaluator implements IGraphics2D, IGraphics3D {

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
    public boolean graphics2D(ObjectNode json, IAST ast, IAST color, IExpr opacity,
        IASTAppendable listOfCoords) {
      IAST list1;
      IAST list2;
      if (ast.argSize() == 0) {
        list1 = F.List(F.C0, F.C0);
        // unit square
        list2 = F.List(F.C1, F.C1);
        return rectangle(json, color, opacity, list1, list2, listOfCoords);
      }
      if ((ast.argSize() == 1 || ast.argSize() == 2) && ast.arg1().isList2()) {
        list1 = (IAST) ast.arg1();
        if (ast.argSize() == 2 && ast.arg2().isList2()) {
          list2 = (IAST) ast.arg1();
        } else {
          // unit square
          list2 = F.List(F.C1, F.C1);
        }
        return rectangle(json, color, opacity, list1, list2, listOfCoords);
      }
      return false;
    }

    private static boolean rectangle(ObjectNode json, IAST color, IExpr opacity, IAST list1,
        IAST list2, IASTAppendable listOfCoords) {
      json.put("type", "rectangle");
      setColor(json, color, F.NIL);
      setOpacity(json, opacity.orElse(F.C1));
      return graphics2DCoords(json, F.List(list1, list2), listOfCoords);
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
    public boolean graphics2DSVG(StringBuilder buf, IAST ast, Dimensions2D dim, IAST color,
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

    private boolean sphere(ObjectNode json, IAST sphereCoords, double sphereRadius, IAST color,
        IExpr opacity) {
      json.put("type", "sphere");
      setColor(json, color, color);
      setOpacity(json, opacity.orElse(F.C1D2));
      json.put("radius", sphereRadius);
      if (sphereCoords.isList3() && graphics3DCoords(json, F.list(sphereCoords))) {
        return true;
      }
      return false;
    }

    @Override
    public boolean graphics3D(ObjectNode json, IAST ast, IAST color, IExpr opacity) {
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
            if (!sphere(json, (IAST) arg, radius, color, opacity)) {
              return false;
            }
            // if (i < list.size() - 1) {
            // buf.append(",");
            // }
          }

          return true;
        }
        return sphere(json, list, radius, color, opacity);
      }
      return false;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class Tetrahedron extends AbstractEvaluator implements IGraphics3D {

    protected void addSubtypeThreejs(ObjectNode json) {
      json.put("subType", "tetrahedron");
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
    public boolean graphics3D(ObjectNode json, IAST ast, IAST color, IExpr opacity) {

      IAST list = F.list(F.list(F.C0, F.C0, F.C0));
      if (ast.argSize() > 0 && ast.arg1().isList()) {
        list = (IAST) ast.arg1();
      }
      json.put("type", "uniformPolyhedron");
      setColor(json, color, F.NIL);
      setOpacity(json, opacity.orElse(F.C1D2));
      addSubtypeThreejs(json);
      if (list.isListOfLists() && graphics3DCoords(json, list)) {
        return true;
      }

      return false;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class Text extends AbstractEvaluator implements IGraphics2D, IGraphics3D {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }


    @Override
    public boolean graphics2D(ObjectNode json, IAST ast, IAST color, IExpr opacity,
        IASTAppendable listOfCoords) {
      if (ast.argSize() == 1) {
        ast = F.Text(ast.arg1(), F.List(F.C0, F.C0));
      }
      if (ast.argSize() == 2 && ast.arg2().isList2()) {
        IAST expr = (IAST) ast.arg1();
        IAST coords = (IAST) ast.arg2();
        json.put("type", "text");
        setColor(json, color, F.NIL);
        setOpacity(json, opacity.orElse(F.C1));
        if (graphics2DCoords(json, F.List(coords), listOfCoords)) {
          return graphicsTexts(json, expr.toString());
        }
      }
      return false;
    }

    @Override
    public boolean graphics3D(ObjectNode json, IAST ast, IAST color, IExpr opacity) {
      if (ast.argSize() == 1) {
        ast = F.Text(ast.arg1(), F.List(F.C0, F.C0, F.C0));
      }
      if (ast.argSize() == 2 && ast.arg2().isList3()) {
        IAST expr = (IAST) ast.arg1();
        IAST coords = (IAST) ast.arg2();
        json.put("type", "text");
        setColor(json, color, F.NIL);
        setOpacity(json, opacity.orElse(F.C1));
        if (graphics3DCoords(json, F.List(coords))) {
          return graphicsTexts(json, expr.toString());
        }
      }
      return false;
    }

    private static boolean graphicsTexts(ObjectNode json, String texts) {
      ArrayNode array = json.arrayNode();
      array.add(texts);
      json.set("texts", array);
      return true;
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
    public boolean graphics3D(ObjectNode json, IAST ast, IAST color, IExpr opacity) {
      if (ast.argSize() > 0 && ast.arg1().isList()) {
        double radius = 0.01;
        if (ast.argSize() == 2) {
          radius = ast.arg2().toDoubleDefault(1.0);
        }
        IAST list = (IAST) ast.arg1();
        json.put("type", "tube");
        setColor(json, color, F.NIL);
        setOpacity(json, opacity.orElse(F.C1));
        json.put("radius", radius);
        if (list.isListOfLists() && graphics3DCoords(json, list)) {
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
            if (evaluator instanceof IGraphics2D) {
              if (!first) {
                buf.append(",");
              }
              first = false;

              if (!((IGraphics2D) evaluator).graphics2DSVG(buf, primitive, dim, rgbColor,
                  opacity)) {
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

  public static boolean exportGraphics3DRecursive(ArrayNode arrayNode, IAST data3D) {
    if (data3D.isList()) {
      // boolean first = true;
      IAST rgbColor = F.NIL;
      IExpr opacity = F.NIL;
      IAST list = data3D;
      for (int i = 1; i < list.size(); i++) {
        IExpr arg = list.get(i);
        if (arg.isAST()) {
          IAST ast = (IAST) arg;
          if (ast.isList()) {
            // StringBuilder primitivesBuffer = new StringBuilder();
            if (exportGraphics3DRecursive(arrayNode, ast)) {
              // if (!first) {
              // buf.append(",");
              // }
              // first = false;
              // buf.append(primitivesBuffer);
            }
          } else if (ast.isRGBColor()) {
            rgbColor = ast;
          } else if (ast.isAST(S.Opacity, 2)) {
            opacity = ast.arg1();
          } else if (ast.head().isBuiltInSymbol()) {
            IBuiltInSymbol symbol = (IBuiltInSymbol) ast.head();
            IEvaluator evaluator = symbol.getEvaluator();
            if (evaluator instanceof IGraphics3D) {
              ObjectNode g = JSON_OBJECT_MAPPER.createObjectNode();
              if (((IGraphics3D) evaluator).graphics3D(g, ast, rgbColor, opacity)) {
                arrayNode.add(g);
                // if (!first) {
                // buf.append(",");
                // }
                // first = false;
                // buf.append(primitivesBuffer);
              }
            }
          }
        }
      }
      return true;
    }
    return false;
  }

  private static boolean graphics2DCoords(ObjectNode json, IAST ast, IASTAppendable listOfCoords,
      double xDelta, double yDelta) {
    ArrayNode array = json.arrayNode();
    for (int i = 1; i < ast.size(); i++) {
      IExpr arg = ast.get(i);
      if (!arg.isList2()) {
        return false;
      }
      IAST coords = (IAST) arg;
      ArrayNode arrayNode0 = json.arrayNode();
      ArrayNode arrayNode = json.arrayNode();
      double xCoord = coords.arg1().evalf();
      double yCoord = coords.arg2().evalf();
      arrayNode.add(xCoord);
      arrayNode.add(yCoord);
      if (listOfCoords.isPresent()) {
        listOfCoords.append(F.List(F.num(xCoord + xDelta), F.num(yCoord + yDelta)));
        listOfCoords.append(F.List(F.num(xCoord - xDelta), F.num(yCoord + yDelta)));
        listOfCoords.append(F.List(F.num(xCoord + xDelta), F.num(yCoord - yDelta)));
        listOfCoords.append(F.List(F.num(xCoord - xDelta), F.num(yCoord - yDelta)));
      }
      arrayNode0.add(arrayNode);
      array.add(arrayNode0);
    }
    json.set("coords", array);
    return true;
  }

  private static boolean graphics2DCoords(ObjectNode json, IAST ast, IASTAppendable listOfCoords) {
    ArrayNode array = json.arrayNode();
    for (int i = 1; i < ast.size(); i++) {
      IExpr arg = ast.get(i);
      if (!arg.isList2()) {
        return false;
      }
      IAST coords = (IAST) arg;
      ArrayNode arrayNode0 = json.arrayNode();
      ArrayNode arrayNode = json.arrayNode();
      double xCoord = coords.arg1().evalf();
      double yCoord = coords.arg2().evalf();
      arrayNode.add(xCoord);
      arrayNode.add(yCoord);
      if (listOfCoords.isPresent()) {
        listOfCoords.append(F.List(F.num(xCoord), F.num(yCoord)));
      }
      arrayNode0.add(arrayNode);
      array.add(arrayNode0);
    }
    json.set("coords", array);
    return true;
  }

  private static boolean graphics3DCoords(ObjectNode json, IAST ast) {
    return graphics3DCoords(json, ast, "coords");
  }

  private static boolean graphics3DCoords(ObjectNode json, IAST ast, String coordStr) {
    ArrayNode array = json.arrayNode();
    for (int i = 1; i < ast.size(); i++) {
      IExpr arg = ast.get(i);
      if (!arg.isList3()) {
        return false;
      }
      IAST coords = (IAST) arg;
      ArrayNode arrayNode0 = json.arrayNode();
      ArrayNode arrayNode = json.arrayNode();
      arrayNode.add(coords.arg1().evalf());
      arrayNode.add(coords.arg2().evalf());
      arrayNode.add(coords.arg3().evalf());
      arrayNode0.add(arrayNode);
      array.add(arrayNode0);

      // IAST coords = (IAST) arg;
      // buf.append("[[");
      // coords.joinToString(buf, ",");
      // buf.append("]]");
      // if (i < ast.size() - 1) {
      // buf.append(",");
      // }
    }
    json.set(coordStr, array);
    return true;
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

  private static boolean graphics3DCoordsOrListOfCoords(ObjectNode json, IAST coordsOrListOfCoords,
      String coordStr) {
    ArrayNode arrayNode = JSON_OBJECT_MAPPER.createArrayNode();
    if (coordsOrListOfCoords.isListOfLists()) {
      final int size = coordsOrListOfCoords.size();
      for (int i = 1; i < size; i++) {
        ArrayNode subArrayNode = JSON_OBJECT_MAPPER.createArrayNode();
        IAST subList = (IAST) coordsOrListOfCoords.get(i);
        for (int j = 1; j < subList.size(); j++) {
          subArrayNode.add(subList.get(j).evalf());
        }
        arrayNode.add(subArrayNode);
      }
    } else if (coordsOrListOfCoords.isList()) {
      ArrayNode subArrayNode = JSON_OBJECT_MAPPER.createArrayNode();
      for (int i = 1; i < coordsOrListOfCoords.size(); i++) {
        subArrayNode.add(coordsOrListOfCoords.get(i).evalf());
      }
      arrayNode.add(subArrayNode);
    } else {
      return false;
    }
    json.set(coordStr, arrayNode);
    return true;
  }

  public static void initialize() {
    Initializer.init();
  }

  public static boolean renderGraphics2D(StringBuilder graphics2DBuffer, IAST graphics2DAST,
      boolean javaScript, EvalEngine engine) {
    IAST arg1 = graphics2DAST.first().makeList();
    // IExpr lighting = S.Automatic;
    final OptionArgs options =
        new OptionArgs(graphics2DAST.topHead(), graphics2DAST, 2, engine, true);
    IAST optionsList = F.List();
    if (options != null) {
      // lighting = options.getOption(S.Lighting).orElse(lighting);
      optionsList = options.getCurrentOptionsList();
    }
    if (arg1.isAST() && arg1.head().isBuiltInSymbol()
        && graphics2DJSON(graphics2DBuffer, arg1, optionsList, javaScript)) {
      return true;
    }
    return false;
  }

  private static boolean graphics2DJSON(StringBuilder graphics2DBuffer, IExpr data2D,
      IAST optionsList, boolean javaScript) {
    ObjectNode json = JSON_OBJECT_MAPPER.createObjectNode();
    ArrayNode arrayNode = JSON_OBJECT_MAPPER.createArrayNode();
    IASTAppendable listOfCoords = F.ListAlloc();
    if (GraphicsFunctions.exportGraphics2DRecursive(arrayNode, (IAST) data2D, listOfCoords)) {
      try {
        if (javaScript) {
          graphics2DBuffer.append("drawGraphics2d(\"graphics2d\",\n");
        }
        json.set("elements", arrayNode);
        if (listOfCoords.argSize() > 0) {
          IExpr coordinateBounds = S.CoordinateBounds.ofNIL(EvalEngine.get(), listOfCoords);
          ObjectNode objectNode = JSON_OBJECT_MAPPER.createObjectNode();
          if (coordinateBounds.isList2()
              && GraphicsFunctions.graphicsExtent(objectNode, (IAST) coordinateBounds, 2)) {
            json.set("extent", objectNode);
          } else {
            // return false;
            // fall through?
          }
        }
        if (optionsList.argSize() > 0) {
          for (int i = 1; i < optionsList.size(); i++) {
            IExpr arg = optionsList.get(i);
            if (arg.isRule()) {
              IAST rule = (IAST) arg;
              IExpr lhs = rule.arg1();
              IExpr rhs = rule.arg2();

              if (lhs == S.Axes) {
                graphics2DAxes(json, rhs, optionsList);
              }
            }
          }

          arrayNode = JSON_OBJECT_MAPPER.createArrayNode();
          if (GraphicsFunctions.exportGraphics2DOptions(arrayNode, optionsList)) {
            if (arrayNode.size() > 0) {
              json.set("options", arrayNode);
            }
          } else {
            return false;
          }
        }


        graphics2DBuffer.append(json.toString());

        // graphics2DBuffer.append("{");
        // // graphics2DBuffer.append("\naxes: {},");
        // graphics2DBuffer.append("elements: [");
        // graphics2DBuffer.append(arrayNode.toString());
        // graphics2DBuffer.append("]");
        // // graphics3DLigthing(graphics2DBuffer, lighting);
        // // graphics2DBuffer.append("\nviewpoint: [1.3, -2.4, 2.0]");
        // graphics2DBuffer.append("}");
        if (javaScript) {
          graphics2DBuffer.append("\n);");
        }
        return true;
      } catch (Exception ex) {
        LOGGER.debug("GraphicsFunctions.renderGraphics2D() failed", ex);
      }
    }
    return false;
  }

  private static void graphics2DAxes(ObjectNode json, IExpr axesOptions, IAST optionsList) {
    ObjectNode g = JSON_OBJECT_MAPPER.createObjectNode();
    if (axesOptions.isList2()) {
      IExpr a1 = axesOptions.first();
      IExpr a2 = axesOptions.second();
      ArrayNode an = JSON_OBJECT_MAPPER.createArrayNode();
      if (a1.isTrue()) {
        an.add(true);
      } else {
        an.add(false);
      }
      if (a2.isTrue()) {
        an.add(true);
      } else {
        an.add(false);
      }
      g.set("hasaxes", an);
    } else if (axesOptions.isTrue()) {
      g.put("hasaxes", true);
    } else {
      g.put("hasaxes", false);
    }
    json.set("axes", g);
  }

  public static boolean exportGraphics2DRecursive(ArrayNode arrayNode, IAST data2D,
      IASTAppendable listOfCoords) {
    if (data2D.isList()) {
      // boolean first = true;
      IAST rgbColor = F.NIL;
      IExpr opacity = F.NIL;
      IAST list = data2D;
      for (int i = 1; i < list.size(); i++) {
        IExpr arg = list.get(i);
        if (arg.isAST()) {
          IAST ast = (IAST) arg;
          if (ast.isList()) {
            StringBuilder primitivesBuffer = new StringBuilder();
            if (exportGraphics2DRecursive(arrayNode, ast, listOfCoords)) {
            }
          } else if (ast.isRGBColor()) {
            rgbColor = ast;
          } else if (ast.isAST(S.Opacity, 2)) {
            opacity = ast.arg1();
          } else if (ast.head().isBuiltInSymbol()) {
            IBuiltInSymbol symbol = (IBuiltInSymbol) ast.head();
            IEvaluator evaluator = symbol.getEvaluator();
            if (evaluator instanceof IGraphics2D) {
              ObjectNode g = JSON_OBJECT_MAPPER.createObjectNode();
              if (((IGraphics2D) evaluator).graphics2D(g, ast, rgbColor, opacity, listOfCoords)) {
                arrayNode.add(g);
              }
            }
          }
        }
      }
      return true;
    }
    return false;
  }

  private static boolean exportGraphics2DOptions(ArrayNode arrayNode, IAST optionsList) {
    for (int i = 1; i < optionsList.size(); i++) {
      IExpr arg = optionsList.get(i);
      if (arg.isRule()) {
        IAST rule = (IAST) arg;
        IExpr lhs = rule.arg1();
        IExpr rhs = rule.arg2();
        ObjectNode g = JSON_OBJECT_MAPPER.createObjectNode();
        if (lhs == S.Axes) {
          continue;
        }
        if (rhs.isSymbol()) {
          if (rhs.isTrue()) {
            g.put(lhs.toString(), true);
          } else if (rhs.isFalse()) {
            g.put(lhs.toString(), false);
          } else {
            g.put(lhs.toString(), rhs.toString());
          }
        } else {
          g.set(lhs.toString(), ExpressionJSONConvert.exportExpressionJSON(rhs));
        }
        arrayNode.add(g);
      } else {
        return false;
      }
    }
    return true;
  }

  private static boolean graphicsExtent(ObjectNode objectNode, IAST extentList, int dimension) {
    for (int i = 1; i < extentList.size(); i++) {
      IExpr arg = extentList.get(i);
      if (arg.isAST(S.List, dimension + 1)) {
        IAST list = (IAST) arg;
        if (dimension == 2) {
          if (i == 1) {
            objectNode.put("xmin", list.arg1().evalf());
            objectNode.put("xmax", list.arg2().evalf());
          } else if (i == 2) {
            objectNode.put("ymin", list.arg1().evalf());
            objectNode.put("ymax", list.arg2().evalf());
          }
          continue;
        } else if (dimension == 3) {
          if (i == 1) {
            objectNode.put("xmin", list.arg1().evalf());
            objectNode.put("xmax", list.arg2().evalf());
          } else if (i == 2) {
            objectNode.put("ymin", list.arg1().evalf());
            objectNode.put("ymax", list.arg2().evalf());
          } else if (i == 3) {
            objectNode.put("zmin", list.arg1().evalf());
            objectNode.put("zmax", list.arg2().evalf());
          }
          continue;
        }
      }
      return false;
    }
    return true;
  }

  public static boolean renderGraphics3D(StringBuilder graphics3DBuffer, IAST graphics3DAST,
      boolean javaScript, EvalEngine engine) {
    IExpr arg1 = graphics3DAST.first();
    if (!arg1.isList()) {
      arg1 = F.list(arg1);
    }
    IExpr lighting = S.Automatic; // .List(F.$str("Auto"), F.RGBColor(F.C1, F.C1, F.C1));
    OptionArgs options = OptionArgs.createOptionArgs(graphics3DAST, engine);
    if (options != null) {
      lighting = options.getOption(S.Lighting).orElse(lighting);

      // if (option.isList1() && option.first().isList() && option.first().first().isString()) {
      // lighting = option.first();
      // }
    }

    if (arg1.isAST() && arg1.head().isBuiltInSymbol()
        && graphics3DJSON(graphics3DBuffer, lighting, arg1, javaScript)) {
      return true;
    }
    return false;
  }

  private static boolean graphics3DJSON(StringBuilder graphics3DBuffer, IExpr lighting,
      IExpr data3D, boolean javaScript) {
    ObjectNode json = JSON_OBJECT_MAPPER.createObjectNode();
    ArrayNode arrayNode = JSON_OBJECT_MAPPER.createArrayNode();
    if (GraphicsFunctions.exportGraphics3DRecursive(arrayNode, (IAST) data3D)) {
      try {
        if (javaScript) {
          graphics3DBuffer.append("drawGraphics3d(document.getElementById('graphics3d'),\n");
        }
        json.set("elements", arrayNode);
        graphics3DLigthing(json, lighting);
        ArrayNode vp = JSON_OBJECT_MAPPER.createArrayNode();
        vp.add(1.3);
        vp.add(-2.4);
        vp.add(2.0);
        json.set("viewpoint", vp);
        graphics3DBuffer.append(json.toString());
        // graphics3DBuffer.append("{");
        // graphics3DBuffer.append("\naxes: {},");
        // graphics3DBuffer.append("\nelements: [");
        // graphics3DBuffer.append(jsonPrimitives.toString());
        // graphics3DBuffer.append("],");
        // graphics3DLigthing(graphics3DBuffer, lighting);
        // graphics3DBuffer.append("\nviewpoint: [1.3, -2.4, 2.0]");
        // graphics3DBuffer.append("}");
        if (javaScript) {
          graphics3DBuffer.append("\n);");
        }
        return true;
      } catch (Exception ex) {
        LOGGER.debug("GraphicsFunctions.renderGraphics3D() failed", ex);
      }
    }
    return false;
  }

  private static void graphics3DLigthing(ObjectNode json, IExpr lighting) {
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
    // graphics3DBuffer.append("\nlighting: [");
    ArrayNode arrayNode = JSON_OBJECT_MAPPER.createArrayNode();
    if (result.isPresent()) {
      if (result.isList()) {
        for (int i = 1; i < result.size(); i++) {
          if (result.get(i).isAST()) {
            // if (lightingDone) {
            // graphics3DBuffer.append(",");
            // }
            ObjectNode g = JSON_OBJECT_MAPPER.createObjectNode();
            if (graphics3DSingleLight(g, (IAST) result.get(i))) {
              arrayNode.add(g);
              lightingDone = true;
            }
          }
        }
      } else {
        ObjectNode g = JSON_OBJECT_MAPPER.createObjectNode();
        if (graphics3DSingleLight(g, result)) {
          arrayNode.add(g);
          lightingDone = true;
        }
      }
    }

    if (!lightingDone) {
      ObjectNode g = JSON_OBJECT_MAPPER.createObjectNode();
      lightingDone = graphics3DSingleLight(g, automatic);
      arrayNode.add(g);
    }
    json.set("lighting", arrayNode);
  }

  private static boolean graphics3DSingleLight(ObjectNode json, IAST result) {
    if (result.isAST1()) {
      IExpr color = result.arg1();
      if (color.isRGBColor()) {
        if (result.head().equals(S.AmbientLight)) {
          json.put("type", "ambient");
          setColor(json, (IAST) color, F.RGBColor(F.C1, F.C1, F.C1));
          return true;
        }
      }
    } else if (result.isAST2()) {
      String name = result.arg1().toString();
      if (name.equals("Ambient")) {
        IExpr color = result.arg2();
        if (color.isRGBColor()) {
          json.put("type", "ambient");
          setColor(json, (IAST) color, F.NIL);
          return true;
        }
      } else {
        IExpr color = result.arg1();
        if (color.isRGBColor() && result.arg2().isList()) {
          IAST list = (IAST) result.arg2();
          if (result.head().equals(S.DirectionalLight) && list.isList()) {
            json.put("type", "directional");
            setColor(json, (IAST) color, F.NIL);
            graphics3DCoordsOrListOfCoords(json, list, "coords");
            return true;
          } else if (result.head().equals(S.PointLight) && list.isList3()) {
            json.put("type", "point");
            setColor(json, (IAST) color, F.NIL);
            graphics3DCoordsOrListOfCoords(json, list, "coords");
            return true;
          } else if (result.head().equals(S.SpotLight) && list.isList2() && list.isListOfLists()) {
            IAST coords = (IAST) list.arg1();
            IAST target = (IAST) list.arg2();
            if (coords.isList3() && target.isList3()) {
              double angle = 1.0;
              if (result.size() == 5) {
                angle = result.arg4().toDoubleDefault(1.0);
              }
              json.put("type", "spot");
              json.put("angle", angle);
              setColor(json, (IAST) color, F.NIL);
              graphics3DCoordsOrListOfCoords(json, coords, "coords");
              graphics3DCoordsOrListOfCoords(json, target, "target");
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
          json.put("type", "directional");
          setColor(json, (IAST) color, F.NIL);
          graphics3DCoordsOrListOfCoords(json, list, "coords");
          return true;
        } else if (name.equals("Point") && list.isList3()) {
          json.put("type", "point");
          setColor(json, (IAST) color, F.NIL);
          graphics3DCoordsOrListOfCoords(json, list, "coords");
          return true;
        } else if (name.equals("Spot") && list.isList2() && list.isListOfLists()) {
          IAST coords = (IAST) list.arg1();
          IAST target = (IAST) list.arg2();
          if (coords.isList3() && target.isList3()) {
            double angle = 1.0;
            if (result.size() == 5) {
              angle = result.arg4().toDoubleDefault(1.0);
            }
            json.put("type", "spot");
            json.put("angle", angle);
            setColor(json, (IAST) color, F.NIL);
            graphics3DCoordsOrListOfCoords(json, coords, "coords");
            graphics3DCoordsOrListOfCoords(json, target, "target");
            return true;
          }
        }
      }
    }
    return false;

  }

  private static void setColor(ObjectNode json, IAST color, IAST defaultColor) {
    if (color.isPresent()) {
      if (color.isAST(S.RGBColor, 4, 5)) {
        if (color.size() == 5) {
          double opacity = color.arg4().toDoubleDefault(1.0);
          json.put("opacity", opacity);
        }
        double red = color.arg1().toDoubleDefault(0.0);
        double green = color.arg2().toDoubleDefault(0.0);
        double blue = color.arg3().toDoubleDefault(0.0);

        ArrayNode arrayNode = json.arrayNode();
        arrayNode.add(red);
        arrayNode.add(green);
        arrayNode.add(blue);
        json.set("color", arrayNode);
        return;
      } else if (color.isAST(S.RGBColor, 1) && color.arg1().isAST(S.List, 4, 5)) {
        IAST list = (IAST) color.arg1();
        if (color.size() == 5) {
          double opacity = list.arg4().toDoubleDefault(1.0);
          json.put("opacity", opacity);
        }
        double red = list.arg1().toDoubleDefault(0.0);
        double green = list.arg2().toDoubleDefault(0.0);
        double blue = list.arg3().toDoubleDefault(0.0);
        ArrayNode arrayNode = json.arrayNode();
        arrayNode.add(red);
        arrayNode.add(green);
        arrayNode.add(blue);
        json.set("color", arrayNode);
        return;
      }
    }
    if (defaultColor.isAST(S.RGBColor, 4)) {
      double red = defaultColor.arg1().toDoubleDefault(0.0);
      double green = defaultColor.arg2().toDoubleDefault(0.0);
      double blue = defaultColor.arg3().toDoubleDefault(0.0);
      ArrayNode arrayNode = json.arrayNode();
      arrayNode.add(red);
      arrayNode.add(green);
      arrayNode.add(blue);
      json.set("color", arrayNode);
    } else {
      ArrayNode arrayNode = json.arrayNode();
      arrayNode.add(1.0);
      arrayNode.add(0.5);
      arrayNode.add(0.0);
      json.set("color", arrayNode);
    }

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

  private static void setOpacity(ObjectNode json, IExpr opacityExpr) {
    double opacity = opacityExpr.toDoubleDefault(1.0);
    json.put("opacity", opacity);
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
          if (evaluator instanceof IGraphics2D) {
            if (!((IGraphics2D) evaluator).graphics2DDimension(primitive, dim)) {
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
        IExpr arg1 = ast.arg1().makeList();
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

  public static void main(String[] args) {
    ArrayNode temp = JSON_OBJECT_MAPPER.createArrayNode();
    ObjectNode json = JSON_OBJECT_MAPPER.createObjectNode();
    // json.set("title", title);
    // json.set("scanner", scanner);
    json.put("error", "false");
    json.put("numsubpods", 1);
    json.putPOJO("subpods", temp);
    ArrayNode array = json.arrayNode();

    ArrayNode arrayNode0 = json.arrayNode();
    ArrayNode arrayNode = json.arrayNode();
    arrayNode.add(2.0);
    arrayNode.add(3.0);
    arrayNode0.add(arrayNode);
    array.add(arrayNode0);

    arrayNode0 = json.arrayNode();
    arrayNode = json.arrayNode();
    arrayNode.add(2.0);
    arrayNode.add(3.0);
    arrayNode0.add(arrayNode);
    array.add(arrayNode0);
    json.set("coords", array);

    System.out.println(json);
  }
}
