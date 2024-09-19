package org.matheclipse.core.builtin;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.OperationSystem;
import org.matheclipse.core.convert.RGBColor;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractSymbolEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.Dimensions2D;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.graphics.IGraphics2D;
import org.matheclipse.core.graphics.IGraphics3D;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class GraphicsFunctions {
  /**
   * From the docs: "Mapper instances are fully thread-safe provided that ALL configuration of the
   * instance occurs before ANY read or write calls."
   */
  public static final ObjectMapper JSON_OBJECT_MAPPER = new ObjectMapper();

  private static final DecimalFormatSymbols US_SYMBOLS = new DecimalFormatSymbols(Locale.US);

  protected static final DecimalFormat FORMATTER = new DecimalFormat("0.0####", US_SYMBOLS);

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
    public boolean graphics2D(ArrayNode arrayNode, IAST ast, GraphicsOptions options) {
      if (ast.argSize() > 0 && ast.arg1().isList()) {
        IAST list = (IAST) ast.arg1();
        ObjectNode g = GraphicsFunctions.JSON_OBJECT_MAPPER.createObjectNode();
        g.put("type", "arrow");
        g.put("thickness", options.thickness());
        if (list.isListOfLists() && graphics2DCoords(g, list, options)) {
          arrayNode.add(g);
          return true;
        }
      }
      return false;
    }

    @Override
    public boolean graphicsComplex2D(ArrayNode arrayNode, IAST ast, IAST listOfIntPositions,
        GraphicsOptions options) {
      if (ast.argSize() > 0) {
        IExpr list = ast.arg1();
        ObjectNode g = GraphicsFunctions.JSON_OBJECT_MAPPER.createObjectNode();
        g.put("type", "arrow");
        options.setColor(g);
        if (graphicsComplex2DPositions(arrayNode, g, list, listOfIntPositions, options)) {
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
        GraphicsOptions.setColor(json, color, F.NIL, true);
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

  private static class Circle extends AbstractEvaluator implements IGraphics2D, IGraphics3D {

    private static boolean circle(ArrayNode arrayNode, String jsonType, IAST circleCoords,
        double circleRadius1, double circleRadius2, double angle1, double angle2,
        GraphicsOptions options) {
      ObjectNode g = GraphicsFunctions.JSON_OBJECT_MAPPER.createObjectNode();
      g.put("type", jsonType);
      g.put("radius1", circleRadius1);
      g.put("radius2", circleRadius2);
      if (angle1 != 0.0 || angle2 != Math.PI * 2) {
        // not a full circle
        g.put("angle1", angle1);
        g.put("angle2", angle2);
      }
      if (circleCoords.isList2()
          && graphics2DCoords(g, F.list(circleCoords), options, circleRadius1, circleRadius2)) {
        arrayNode.add(g);
        return true;
      }
      return false;
    }

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
    public boolean graphics2D(ArrayNode arrayNode, IAST ast, GraphicsOptions options) {
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
            if (!circle(arrayNode, getJSONType(), (IAST) arg, radius1, radius2, angle1, angle2,
                options)) {
              return false;
            }
          }
          return true;
        }
        if (circle(arrayNode, getJSONType(), list, radius1, radius2, angle1, angle2, options)) {
          return true;
        }
      }
      return false;
    }

    @Override
    public boolean graphicsComplex2D(ArrayNode arrayNode, IAST ast, IAST listOfIntPositions,
        GraphicsOptions options) {
      if (ast.argSize() > 0) {
        IExpr list = ast.arg1();
        ObjectNode g = GraphicsFunctions.JSON_OBJECT_MAPPER.createObjectNode();
        g.put("type", getJSONType());
        options.setColor(g);
        if (graphicsComplex2DPositions(arrayNode, g, list, listOfIntPositions, options)) {
          return true;
        }
      }
      return false;
    }

    @Override
    public boolean graphicsComplex2DPositions(ObjectNode json, IAST ast, IAST listOfIntPositions,
        GraphicsOptions options) {
      ArrayNode array = json.arrayNode();
      for (int i = 1; i < ast.size(); i++) {
        IExpr arg = ast.get(i);
        if (arg.isList()) {
          ObjectNode g = GraphicsFunctions.JSON_OBJECT_MAPPER.createObjectNode();
          if (!graphicsComplex2DPositions(g, (IAST) arg, listOfIntPositions, options)) {
            return false;
          }
          array.add(g);
        } else if (arg.isInteger()) {
          int iValue = arg.toIntDefault();
          if (iValue <= 0) {
            return false;
          }

          if (listOfIntPositions.size() < iValue || !listOfIntPositions.get(iValue).isList2()) {
            return false;
          }
          IAST point = (IAST) listOfIntPositions.get(iValue);
          double xCoord = point.first().evalf();
          double yCoord = point.second().evalf();
          double xDelta = 1.0;
          double yDelta = 1.0;
          double[] boundingbox =
              new double[] {xCoord - xDelta, xCoord + xDelta, yCoord - yDelta, yCoord + yDelta};
          options.setBoundingBoxScaled(boundingbox);
          array.add(iValue);
        }
      }
      json.set("positions", array);

      return true;
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

          double cx = ((IReal) list1.arg1()).doubleValue();
          double cy = ((IReal) list1.arg2()).doubleValue();
          double rx = 1.0;
          double ry = 1.0;

          dim.minMax(cx - rx, cx + rx, cy - ry, cy + ry);
          return true;
        }
      } else if (ast.size() == 3 && ast.arg1().isAST(S.List, 3) && ast.arg2().isAST(S.List, 3)) {
        IAST list1 = (IAST) ast.arg1();
        IAST list2 = (IAST) ast.arg2();

        double cx = ((IReal) list1.arg1()).doubleValue();
        double cy = ((IReal) list1.arg2()).doubleValue();
        double rx = ((IReal) list2.arg1()).doubleValue();
        double ry = ((IReal) list2.arg2()).doubleValue();

        dim.minMax(cx - rx, cx + rx, cy - ry, cy + ry);
        return true;
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
        GraphicsOptions.setColor(json, color, F.NIL, true);
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

  private static class Cube extends AbstractEvaluator implements IGraphics3D {

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
      if (ast.isAST0()) {
        ast = F.Cube(F.List(0, 0, 0), F.C1);
      } else if (ast.isAST1()) {
        ast = F.Cube(F.List(0, 0, 0), ast.arg1());
      }

      if (ast.arg1().isList3()) {
        IAST list = (IAST) ast.arg1();
        double x = list.arg1().evalf();
        double y = list.arg1().evalf();
        double z = list.arg1().evalf();
        double halfLength = ast.arg2().evalf() / 2.0;

        json.put("type", "cuboid");
        GraphicsOptions.setColor(json, color, F.NIL, true);
        setOpacity(json, opacity.orElse(F.C1D2));
        if (graphics3DCoords(json, F.Cuboid(//
            F.List(x - halfLength, y - halfLength, z - halfLength), //
            F.List(x + halfLength, y + halfLength, z + halfLength) //
        ))) {
          return true;
        }
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
        GraphicsOptions.setColor(json, color, F.NIL, true);
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
        GraphicsOptions.setColor(json, color, F.NIL, true);
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

  private static class GraphicsComplex extends AbstractEvaluator
      implements IGraphics2D, IGraphics3D {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public boolean graphics2D(ArrayNode arrayNode, IAST ast, GraphicsOptions options) {
      if (ast.argSize() == 2 && ast.arg1().isList()) {
        IAST list = (IAST) ast.arg1();
        IAST primitives = ast.arg2().makeList();
        ObjectNode g = GraphicsFunctions.JSON_OBJECT_MAPPER.createObjectNode();
        g.put("type", "graphicscomplex");
        if (list.isListOfLists() && graphics2DCoords(g, list, options)) {
          arrayNode.add(g);
          ArrayNode array = GraphicsFunctions.JSON_OBJECT_MAPPER.createArrayNode();
          for (int i = 1; i < primitives.size(); i++) {
            IExpr primitive = primitives.get(i);
            if (primitive.isAST() && primitive.isBuiltInFunction()) {
              IBuiltInSymbol symbol = (IBuiltInSymbol) primitive.head();
              IEvaluator evaluator = symbol.getEvaluator();
              if (evaluator instanceof IGraphics2D) {
                if (((IGraphics2D) evaluator).graphicsComplex2D(array, (IAST) primitive, list,
                    options)) {
                }
              } else if (primitive.isRGBColor()) {
                options.setRGBColor((IAST) primitive);
              }
            }
          }
          g.set("data", array);
          return true;
        }
      }
      return false;
    }

    @Override
    public boolean graphics3D(ObjectNode json, IAST ast, IAST color, IExpr opacity) {
      if (ast.argSize() == 2 && ast.arg1().isList()) {
        IAST list = (IAST) ast.arg1();
        IAST primitives = ast.arg2().makeList();
        json.put("type", "graphicscomplex");
        if (list.isListOfLists() && graphics3DCoords(json, list)) {
          ArrayNode array = GraphicsFunctions.JSON_OBJECT_MAPPER.createArrayNode();
          for (int i = 1; i < primitives.size(); i++) {
            IExpr primitive = primitives.get(i);
            if (primitive.isAST() && primitive.isBuiltInFunction()) {
              IBuiltInSymbol symbol = (IBuiltInSymbol) primitive.head();
              IEvaluator evaluator = symbol.getEvaluator();
              if (evaluator instanceof IGraphics3D) {
                ObjectNode g = GraphicsFunctions.JSON_OBJECT_MAPPER.createObjectNode();
                if (((IGraphics3D) evaluator).graphics3D(g, (IAST) primitive, color, opacity)) {
                  array.add(g);
                }
              }
            }
          }
          json.set("elements", array);
          return true;
        }
      }
      return false;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
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

  private static class GraphicsJSON extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isAST(S.Graphics)) {
        StringBuilder graphics2DBuffer = new StringBuilder();
        if (renderGraphics2D(graphics2DBuffer, (IAST) arg1, false, true, engine)) {
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
      S.Dashed.setEvaluator(new Dashed());
      S.DotDashed.setEvaluator(new DotDashed());
      S.Dotted.setEvaluator(new Dotted());


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
      S.Point.setEvaluator(new Point());
      S.Polygon.setEvaluator(new Polygon());
      S.Rectangle.setEvaluator(new Rectangle());
      S.Scaled.setEvaluator(new Scaled());
      S.Sphere.setEvaluator(new Sphere());
      S.Tetrahedron.setEvaluator(new Tetrahedron());
      S.Text.setEvaluator(new Text());
      S.Tube.setEvaluator(new Tube());
      S.Volume.setEvaluator(new Volume());
      S.GraphicsComplex.setEvaluator(new GraphicsComplex());
      S.GraphicsJSON.setEvaluator(new GraphicsJSON());
      S.Graphics3DJSON.setEvaluator(new Graphics3DJSON());
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
    public boolean graphics2D(ArrayNode arrayNode, IAST ast, GraphicsOptions options) {

      IExpr arg1 = ast.arg1().normal(false);
      if (ast.argSize() > 0 && arg1.isList()) {
        IAST list = (IAST) arg1;
        boolean allLists = true;
        for (int i = 1; i < list.size(); i++) {
          IExpr element = list.get(i);
          if (!element.isListOfLists()) {
            allLists = false;
            break;
          }
          ObjectNode g = GraphicsFunctions.JSON_OBJECT_MAPPER.createObjectNode();
          g.put("type", "line");
          if (graphics2DCoords(g, (IAST) element, options)) {
            arrayNode.add(g);
          }
        }
        if (allLists) {
          return true;
        }
        ObjectNode g = GraphicsFunctions.JSON_OBJECT_MAPPER.createObjectNode();
        g.put("type", "line");
        if (list.isListOfLists() && graphics2DCoords(g, list, options)) {
          arrayNode.add(g);
          return true;
        }
      }
      return false;
    }

    @Override
    public boolean graphicsComplex2D(ArrayNode arrayNode, IAST ast, IAST listOfIntPositions,
        GraphicsOptions options) {
      if (ast.argSize() > 0) {
        IExpr list = ast.arg1();
        ObjectNode g = GraphicsFunctions.JSON_OBJECT_MAPPER.createObjectNode();
        g.put("type", "line");
        options.setColor(g);
        if (graphicsComplex2DPositions(arrayNode, g, list, listOfIntPositions, options)) {
          return true;
        }
      }
      return false;
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
            x[i] = ((IReal) point.first()).doubleValue();
            if (x[i] < xMin) {
              xMin = x[i];
            }
            if (x[i] > xMax) {
              xMax = x[i];
            }
            y[i] = ((IReal) point.second()).doubleValue();
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
    public boolean graphics3D(ObjectNode json, IAST ast, IAST color, IExpr opacity) {
      if (ast.argSize() > 0 && ast.arg1().isList()) {
        IAST list = (IAST) ast.arg1();
        json.put("type", "line");
        GraphicsOptions.setColor(json, color, F.NIL, true);
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

    private static void singlePointDimensions(IAST point, Dimensions2D dim) {
      double x1 = ((IReal) point.arg1()).doubleValue();
      double y1 = ((IReal) point.arg2()).doubleValue();

      dim.minMax(x1 - Config.DOUBLE_TOLERANCE, x1 + Config.DOUBLE_TOLERANCE,
          y1 - Config.DOUBLE_TOLERANCE, y1 + Config.DOUBLE_TOLERANCE);
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }

    @Override
    public boolean graphics2D(ArrayNode arrayNode, IAST ast, GraphicsOptions options) {
      if (ast.argSize() > 0 && ast.arg1().isList()) {
        IAST list = (IAST) ast.arg1();
        ObjectNode g = GraphicsFunctions.JSON_OBJECT_MAPPER.createObjectNode();
        g.put("type", "point");
        if (list.isList()) {
          if (list.isListOfLists()) {
            if (graphics2DCoords(g, list, options)) {
              arrayNode.add(g);
              return true;
            }
          }
          if (list.isList2()) {
            if (graphics2DCoords(g, F.List(list), options)) {
              arrayNode.add(g);
              return true;
            }
          }
        }
      }
      return false;
    }

    @Override
    public boolean graphicsComplex2D(ArrayNode arrayNode, IAST ast, IAST listOfIntPositions,
        GraphicsOptions options) {
      if (ast.argSize() > 0) {
        IExpr list = ast.arg1();
        ObjectNode g = GraphicsFunctions.JSON_OBJECT_MAPPER.createObjectNode();
        g.put("type", "point");
        options.setColor(g);
        if (graphicsComplex2DPositions(arrayNode, g, list, listOfIntPositions, options)) {
          return true;
        }
      }
      return false;
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
    public boolean graphics3D(ObjectNode json, IAST ast, IAST color, IExpr opacity) {
      if (ast.argSize() > 0 && ast.arg1().isList()) {
        IAST list = (IAST) ast.arg1();
        json.put("type", "point");
        GraphicsOptions.setColor(json, color, F.RGBColor(F.C0, F.C0, F.C0), true);
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
    public boolean graphics2D(ArrayNode arrayNode, IAST ast, GraphicsOptions options) {
      if (ast.argSize() > 0 && ast.arg1().isList()) {
        IAST list = (IAST) ast.arg1();
        ObjectNode g = GraphicsFunctions.JSON_OBJECT_MAPPER.createObjectNode();
        g.put("type", "polygon");
        if (list.isListOfLists() && graphics2DCoords(g, list, options)) {
          arrayNode.add(g);
          return true;
        }
      }
      return false;
    }

    @Override
    public boolean graphicsComplex2D(ArrayNode arrayNode, IAST ast, IAST listOfIntPositions,
        GraphicsOptions options) {
      if (ast.argSize() > 0 && ast.arg1().isList()) {
        IAST list = (IAST) ast.arg1();
        ObjectNode g = GraphicsFunctions.JSON_OBJECT_MAPPER.createObjectNode();
        g.put("type", "polygon");
        options.setColor(g);
        if (list.isList() && graphicsComplex2DPositions(g, list, listOfIntPositions, options)) {
          arrayNode.add(g);
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
        GraphicsOptions.setColor(json, color, F.NIL, true);
        setOpacity(json, opacity.orElse(F.C1));
        if (list.isListOfLists() && graphics3DCoords(json, list)) {
          return true;
        }
      }
      return false;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.NHOLDREST);
    }
  }

  private static class Rectangle extends AbstractEvaluator implements IGraphics2D, IGraphics3D {

    private static boolean rectangle(ArrayNode arrayNode, IAST list1, IAST list2,
        GraphicsOptions options) {
      ObjectNode g = GraphicsFunctions.JSON_OBJECT_MAPPER.createObjectNode();
      g.put("type", "rectangle");
      if (graphics2DCoords(g, F.List(list1, list2), options)) {
        arrayNode.add(g);
        return true;
      }
      return false;
    }

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
    public boolean graphics2D(ArrayNode arrayNode, IAST ast, GraphicsOptions options) {
      IAST list1;
      IAST list2;
      if (ast.argSize() == 0) {
        list1 = F.List(F.C0, F.C0);
        // unit square
        list2 = F.List(F.C1, F.C1);
        return rectangle(arrayNode, list1, list2, options);
      }
      if ((ast.argSize() == 1 || ast.argSize() == 2) && ast.arg1().isList2()) {
        list1 = (IAST) ast.arg1();
        if (ast.argSize() == 2 && ast.arg2().isList2()) {
          list2 = (IAST) ast.arg2();
        } else {
          // unit square
          list2 = F.List(list1.arg1().add(F.C1), list1.arg2().add(F.C1));
        }
        return rectangle(arrayNode, list1, list2, options);
      }
      return false;
    }

    @Override
    public boolean graphicsComplex2D(ArrayNode arrayNode, IAST ast, IAST listOfIntPositions,
        GraphicsOptions options) {
      if (ast.argSize() > 0) {
        IExpr list = ast.arg1();
        ObjectNode g = GraphicsFunctions.JSON_OBJECT_MAPPER.createObjectNode();
        g.put("type", "rectangle");
        options.setColor(g);
        if (graphicsComplex2DPositions(arrayNode, g, list, listOfIntPositions, options)) {
          return true;
        }
      }
      return false;
    }

    @Override
    public boolean graphics2DDimension(IAST ast, Dimensions2D dim) {
      if (ast.size() == 2) {
        if (ast.arg1().isList2()) {
          IAST list1 = (IAST) ast.arg1();

          double x1 = ((IReal) list1.arg1()).doubleValue();
          double y1 = ((IReal) list1.arg2()).doubleValue();
          double x2 = x1 + 1.0;
          double y2 = y1 + 1.0;

          dim.minMax(x1, x2, y1, y2);
          return true;
        }
      } else if (ast.size() == 3 && ast.arg1().isList2() && ast.arg2().isList2()) {
        IAST list1 = (IAST) ast.arg1();
        IAST list2 = (IAST) ast.arg2();

        double x1 = ((IReal) list1.arg1()).doubleValue();
        double y1 = ((IReal) list1.arg2()).doubleValue();
        double x2 = ((IReal) list2.arg1()).doubleValue();
        double y2 = ((IReal) list2.arg2()).doubleValue();

        dim.minMax(x1, x2, y1, y2);
        return true;
      }
      return false;
    }

    // private static boolean graphics2DCoords(ObjectNode json, IAST minCoord, IAST maxCoord,
    // GraphicsOptions options) {
    // ArrayNode array = json.arrayNode();
    // for (int i = 1; i < minCoord.size(); i++) {
    // IExpr arg = minCoord.get(i);
    // if (!arg.isList2()) {
    // return false;
    // }
    // IAST coords = (IAST) arg;
    // ArrayNode arrayNode0 = json.arrayNode();
    // ArrayNode arrayNode = json.arrayNode();
    // double xCoord = coords.arg1().evalf();
    // double yCoord = coords.arg2().evalf();
    // arrayNode.add(xCoord);
    // arrayNode.add(yCoord);
    // options.setBoundingBoxScaled(xCoord, yCoord);
    // arrayNode0.add(arrayNode);
    // array.add(arrayNode0);
    // }
    // json.set("coords", array);
    // return true;
    // }

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

    private boolean sphere(ObjectNode json, IAST sphereCoords, double sphereRadius, IAST color,
        IExpr opacity) {
      json.put("type", "sphere");
      GraphicsOptions.setColor(json, color, color, true);
      setOpacity(json, opacity.orElse(F.C1D2));
      json.put("radius", sphereRadius);
      if (sphereCoords.isList3() && graphics3DCoords(json, F.list(sphereCoords))) {
        return true;
      }
      return false;
    }
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
      GraphicsOptions.setColor(json, color, F.NIL, true);
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

    private static boolean graphicsTexts(ObjectNode json, String texts) {
      ArrayNode array = json.arrayNode();
      array.add(texts);
      json.set("texts", array);
      return true;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public boolean graphics2D(ArrayNode arrayNode, IAST ast, GraphicsOptions options) {
      if (ast.argSize() == 1) {
        ast = F.Text(ast.arg1(), F.List(F.C0, F.C0));
      }
      if (ast.argSize() == 2 && ast.arg2().isList2()) {
        IExpr expr = ast.arg1();
        IAST coords = (IAST) ast.arg2();
        ObjectNode g = GraphicsFunctions.JSON_OBJECT_MAPPER.createObjectNode();
        g.put("type", "text");
        if (graphics2DCoords(g, F.List(coords), options) && graphicsTexts(g, expr.toString())) {
          arrayNode.add(g);
          return true;
        }
      }
      return false;
    }

    @Override
    public boolean graphicsComplex2D(ArrayNode arrayNode, IAST ast, IAST listOfIntPositions,
        GraphicsOptions options) {
      if (ast.argSize() > 0) {
        IExpr expr = ast.arg1();
        IExpr list = ast.arg2();
        ObjectNode g = GraphicsFunctions.JSON_OBJECT_MAPPER.createObjectNode();
        g.put("type", "text");
        options.setColor(g);
        if (graphicsComplex2DPositions(arrayNode, g, list, listOfIntPositions, options)
            && graphicsTexts(g, expr.toString())) {
          return true;
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
        GraphicsOptions.setColor(json, color, F.NIL, true);
        setOpacity(json, opacity.orElse(F.C1));
        if (graphics3DCoords(json, F.List(coords))) {
          return graphicsTexts(json, expr.toString());
        }
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
    public boolean graphics3D(ObjectNode json, IAST ast, IAST color, IExpr opacity) {
      if (ast.argSize() > 0 && ast.arg1().isList()) {
        double radius = 0.01;
        if (ast.argSize() == 2) {
          radius = ast.arg2().toDoubleDefault(1.0);
        }
        IAST list = (IAST) ast.arg1();
        json.put("type", "tube");
        GraphicsOptions.setColor(json, color, F.NIL, true);
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
        } else if (graphic.isAST(S.Cylinder, 2, 3) && graphic.first().isList(new int[] {2, 3})) {
          IExpr r = F.C1;
          if (graphic.size() == 3) {
            r = graphic.second();
          }
          IAST l1 = (IAST) graphic.first().first();
          IExpr a = l1.arg1();
          IExpr b = l1.arg2();
          IExpr c = l1.arg3();
          IAST l2 = (IAST) graphic.first().second();
          IExpr d = l2.arg1();
          IExpr e = l2.arg2();
          IExpr f = l2.arg3();
          // Sqrt((a-d)^2+(b-e)^2+(c-f)^2)*Pi*r^2
          return F.Times(F.Sqrt(F.Plus(//
              F.Power(F.Subtract(a, d), F.C2), //
              F.Power(F.Subtract(b, e), F.C2), //
              F.Power(F.Subtract(c, f), F.C2))), //
              F.Pi, F.Sqr(r));


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

  public static boolean exportGraphics2D(ObjectNode objectNode, ArrayNode arrayNode, IAST data2D,
      GraphicsOptions graphicsOptions) {
    if (data2D.isList()) {
      // GraphicsOptions graphicsOptions = new GraphicsOptions(EvalEngine.get());
      // graphicsOptions.setOptions(options);
      GraphicsOptions.optionDouble(arrayNode, "opacity", graphicsOptions.opacity());
      GraphicsOptions.optionDouble(arrayNode, "pointSize", graphicsOptions.pointSize());
      // TODO delete textSize because of renaming to fontSize in next version
      // GraphicsOptions.optionInt(arrayNode, "textSize", graphicsOptions.fontSize());
      GraphicsOptions.optionInt(arrayNode, "fontSize", graphicsOptions.fontSize());
      // ObjectNode blackJSON = JSON_OBJECT_MAPPER.createObjectNode();
      GraphicsOptions.setColorOption(arrayNode, GraphicsOptions.BLACK);
      // arrayNode.add(blackJSON);

      // graphicsOptions.graphics2DScalingFunctions(arrayNode);
      graphicsOptions.graphics2DAxes(objectNode);
      graphics2DAspectRatio(arrayNode, graphicsOptions.options());
      graphicsOptions.graphics2DFilling(arrayNode);

      IAST list = data2D;
      return export2DRecursive(arrayNode, list, 1, data2D.size(), graphicsOptions);
    }
    return false;

  }

  public static boolean export2DRecursive(ArrayNode arrayNode, IAST list, int startPosition,
      int endPosition, GraphicsOptions graphicsOptions) {
    for (int i = startPosition; i < endPosition; i++) {
      IExpr arg = list.get(i);
      if (arg.isAST()) {
        IAST ast = (IAST) arg;
        if (ast.isList()) {
          if (export2DRecursive(arrayNode, ast, 1, ast.size(), graphicsOptions)) {
          }
        } else if (ast.isASTSizeGE(S.Style, 2)) {
          GraphicsOptions styledGraphicsOptions = graphicsOptions.copy();
          if (export2DRecursive(arrayNode, ast, 2, ast.size(), styledGraphicsOptions)) {
            if (export2DRecursive(arrayNode, ast, 1, 2, styledGraphicsOptions)) {
            }
          }
        } else if (ast.isASTSizeGE(S.EdgeForm, 2)) {
          IAST edgeFormList = ast.arg1().makeList();
          ObjectNode edgeForm = JSON_OBJECT_MAPPER.createObjectNode();
          ObjectNode edgeList = JSON_OBJECT_MAPPER.createObjectNode();
          for (int j = 1; j < edgeFormList.size(); j++) {
            IExpr expr = edgeFormList.get(i);
            if (expr.isAST(S.Opacity, 2)) {
              ObjectNode g = JSON_OBJECT_MAPPER.createObjectNode();
              double opacity = expr.toDoubleDefault(1.0);
              edgeList.put("opacity", opacity);
            } else if (expr.isAST(S.RGBColor)) {
              ObjectNode g = JSON_OBJECT_MAPPER.createObjectNode();
              if (expr.isAST(S.RGBColor, 4, 5)) {
                double red = ((IAST) expr).arg1().toDoubleDefault(0.0);
                double green = ((IAST) expr).arg2().toDoubleDefault(0.0);
                double blue = ((IAST) expr).arg3().toDoubleDefault(0.0);
                ArrayNode array = g.arrayNode();
                array.add(red);
                array.add(green);
                array.add(blue);
                edgeList.set("color", array);
              } else if (expr.isAST(S.RGBColor, 1) && expr.first().isAST(S.List, 4)) {
                IAST list4 = (IAST) expr.first();
                double red = list4.arg1().toDoubleDefault(0.0);
                double green = list4.arg2().toDoubleDefault(0.0);
                double blue = list4.arg3().toDoubleDefault(0.0);
                ArrayNode array = g.arrayNode();
                array.add(red);
                array.add(green);
                array.add(blue);
                edgeList.set("color", array);
              }
            } else if (expr.isAST(S.GrayLevel, 2, 3)) {
              RGBColor rgb = null;
              IAST grayLevel = (IAST) expr;
              if (grayLevel.isAST1() || grayLevel.isAST2()) {
                ObjectNode g = JSON_OBJECT_MAPPER.createObjectNode();
                rgb = RGBColor.getGrayLevel((float) grayLevel.arg1().evalf());
                ArrayNode array = g.arrayNode();
                array.add(rgb.getRed() / 255.0);
                array.add(rgb.getGreen() / 255.0);
                array.add(rgb.getBlue() / 255.0);
                edgeList.set("color", array);

                if (grayLevel.isAST2()) {
                  g = JSON_OBJECT_MAPPER.createObjectNode();
                  double opacity = grayLevel.arg2().toDoubleDefault(1.0);
                  edgeList.put("opacity", opacity);
                }
              }
            }
          }
          edgeForm.set("edgeForm", edgeList);
          arrayNode.add(edgeForm);
        } else if (ast.isAST(S.Hue, 2, 5)) {
          ObjectNode g = JSON_OBJECT_MAPPER.createObjectNode();
          if (graphicsOptions.setHueColor(arrayNode, ast)) {
            arrayNode.add(g);
          }
        } else if (ast.isAST(S.GrayLevel, 2, 3)) {
          ObjectNode g = JSON_OBJECT_MAPPER.createObjectNode();
          if (GraphicsOptions.setGrayLevel(g, ast)) {
            arrayNode.add(g);
          }
          double opacity = 1.0;
          if (ast.isAST2()) {
            opacity = ast.arg2().evalf();
          }
          GraphicsOptions.optionDouble(arrayNode, "opacity", opacity);
        } else if (ast.isRGBColor()) {
          // ObjectNode g = JSON_OBJECT_MAPPER.createObjectNode();
          GraphicsOptions.setColorOption(arrayNode, ast);
          // arrayNode.add(g);
        } else if (ast.isAST(S.Opacity, 2)) {
          double opacity = graphicsOptions.opacity();
          try {
            opacity = ast.arg1().evalf();
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            //
          }
          GraphicsOptions.optionDouble(arrayNode, "opacity", opacity);
        } else if (ast.isAST(S.PointSize, 2)) {
          double pointSize = graphicsOptions.pointSize(ast);
          GraphicsOptions.optionDouble(arrayNode, "pointSize", pointSize);
        } else if (ast.isAST(S.Dashing, 2) && ast.arg1().isList()) {
          IAST dashingList = (IAST) ast.arg1();
          // TODO
        } else if (ast.isBuiltInFunction()) {
          IBuiltInSymbol symbol = (IBuiltInSymbol) ast.head();
          IEvaluator evaluator = symbol.getEvaluator();
          if (evaluator instanceof IGraphics2D) {
            if (((IGraphics2D) evaluator).graphics2D(arrayNode, ast, graphicsOptions)) {
            }
          }
        }
      }
    }
    return true;
  }

  public static boolean exportGraphics3DRecursive(ArrayNode arrayNode, IAST data3D) {
    if (data3D.isList()) {
      // boolean first = true;
      IAST rgbColor = F.RGBColor(1.0, 0.5, 0.0);
      IExpr opacity = F.num(1.0);
      IAST list = data3D;
      for (int i = 1; i < list.size(); i++) {
        IExpr arg = list.get(i);
        if (arg.isAST()) {
          IAST ast = (IAST) arg;
          if (ast.isList()) {
            if (exportGraphics3DRecursive(arrayNode, ast)) {
            }
          } else if (ast.isAST(S.Hue, 2, 5)) {
            IAST hueColor = ast;
            RGBColor rgb = RGBColor.hueToRGB(hueColor);
            if (hueColor.argSize() == 4) {
              opacity = ast.arg4();
            }
            if (rgb != null) {
              rgbColor =
                  F.RGBColor(rgb.getRed() / 255.0, rgb.getGreen() / 255.0, rgb.getBlue() / 255.0);
            }
          } else if (ast.isRGBColor()) {
            rgbColor = ast;
          } else if (ast.isAST(S.Opacity, 2)) {
            opacity = ast.arg1();
          } else if (ast.isBuiltInFunction()) {
            IBuiltInSymbol symbol = (IBuiltInSymbol) ast.head();
            IEvaluator evaluator = symbol.getEvaluator();
            if (evaluator instanceof IGraphics3D) {
              ObjectNode g = JSON_OBJECT_MAPPER.createObjectNode();
              if (((IGraphics3D) evaluator).graphics3D(g, ast, rgbColor, opacity)) {
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

  public static void graphics2DAspectRatio(ArrayNode arrayNode, OptionArgs options) {
    IExpr option;
    option = options.getOption(S.AspectRatio);
    if (option.isPresent()) {
      ObjectNode g = JSON_OBJECT_MAPPER.createObjectNode();
      IReal value = option.evalReal();
      if (value != null) {
        g.put("factor", option.evalf());
      } else {
        g.put("symbol", option.toString().toLowerCase(Locale.US));
      }
      ObjectNode aspectRatio = JSON_OBJECT_MAPPER.createObjectNode();
      aspectRatio.set("aspectRatio", g);
      arrayNode.add(aspectRatio);
    } else {
      ObjectNode g = JSON_OBJECT_MAPPER.createObjectNode();
      g.put("symbol", "automatic");
      ObjectNode aspectRatio = JSON_OBJECT_MAPPER.createObjectNode();
      aspectRatio.set("aspectRatio", g);
      arrayNode.add(aspectRatio);
    }
  }

  private static boolean graphics2DCoords(ObjectNode json, IAST ast, GraphicsOptions options) {
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
      options.setBoundingBoxScaled(xCoord, yCoord);
      arrayNode0.add(arrayNode);
      array.add(arrayNode0);
    }
    json.set("coords", array);
    return true;
  }

  private static boolean graphics2DCoords(ObjectNode json, IAST ast, GraphicsOptions options,
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
      double[] boundingbox =
          new double[] {xCoord - xDelta, xCoord + xDelta, yCoord - yDelta, yCoord + yDelta};
      options.setBoundingBoxScaled(boundingbox);
      arrayNode0.add(arrayNode);
      array.add(arrayNode0);
    }
    json.set("coords", array);
    return true;
  }

  private static boolean graphics2DJSON(StringBuilder graphics2DBuffer, IExpr data2D,
      OptionArgs options, boolean javaScript, boolean prettyPrint) {
    ObjectNode json = JSON_OBJECT_MAPPER.createObjectNode();
    ArrayNode arrayNode = JSON_OBJECT_MAPPER.createArrayNode();
    GraphicsOptions graphicsOptions = new GraphicsOptions(EvalEngine.get());
    graphicsOptions.setOptions(options);
    IExpr plotRange = options.getOption(S.PlotRange);

    if (GraphicsFunctions.exportGraphics2D(json, arrayNode, (IAST) data2D, graphicsOptions)) {
      try {
        if (javaScript) {
          graphics2DBuffer.append("drawGraphics2d(\"graphics2d\",\n");
        }
        json.set("elements", arrayNode);

        options = graphicsOptions.options();
        ObjectNode objectNode = JSON_OBJECT_MAPPER.createObjectNode();

        // IExpr coordinateBounds = S.CoordinateBounds.ofNIL(EvalEngine.get(), listOfCoords);
        IExpr option = options.getOption(S.PlotRange);
        int[] matrix = option.isMatrix();
        if (matrix != null && matrix[0] == 2 && matrix[1] == 2) {
          if (graphicsOptions.graphicsExtent2D(objectNode, (IAST) option)) {
            json.set("extent", objectNode);
          }
        } else {
          if (graphicsOptions.graphicsExtent2D(objectNode)) {
            json.set("extent", objectNode);
          } else {
            // return false;
            // fall through?
          }
        }

        if (plotRange.isPresent()
            && graphicsOptions.graphicsExtent2D(objectNode, (IAST) plotRange)) {
          json.set("extent", objectNode);
        }

        // if (options.size() > 0) {
        // IAST optionsList = F.List();
        // // lighting = options.getOption(S.Lighting).orElse(lighting);
        // optionsList = options.getCurrentOptionsList();
        //
        // for (int i = 1; i < optionsList.size(); i++) {
        // IExpr arg = optionsList.get(i);
        // if (arg.isRule()) {
        // IAST rule = (IAST) arg;
        // IExpr lhs = rule.arg1();
        // IExpr rhs = rule.arg2();
        //
        // if (lhs == S.Axes) {
        // axesDefined = graphics2DAxes(json, options);
        // }
        // }
        // }
        //
        // arrayNode = JSON_OBJECT_MAPPER.createArrayNode();
        // if (GraphicsFunctions.exportGraphics2DOptions(arrayNode, optionsList)) {
        // if (arrayNode.size() > 0) {
        // json.set("options", arrayNode);
        // }
        // } else {
        // return false;
        // }
        // }
        if (prettyPrint) {
          graphics2DBuffer.append(json.toPrettyString());
        } else {
          graphics2DBuffer.append(json.toString());
        }
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
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        Errors.printMessage(S.Graphics, rex, EvalEngine.get());
      }
    }
    return false;
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

  // private static boolean graphics3DCoords(StringBuilder buf, IAST ast) {
  // return graphics3DCoords(buf, ast, "coords");
  // }

  // private static boolean graphics3DCoords(StringBuilder buf, IAST ast, String coordStr) {
  // buf.append(coordStr + ": [");
  //
  // for (int i = 1; i < ast.size(); i++) {
  // IExpr arg = ast.get(i);
  // if (!arg.isList3()) {
  // return false;
  // }
  // IAST coords = (IAST) arg;
  // buf.append("[[");
  // coords.joinToString(buf, ",");
  // buf.append("]]");
  // if (i < ast.size() - 1) {
  // buf.append(",");
  // }
  // }
  // buf.append("]");
  // return true;
  // }

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
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        Errors.printMessage(S.Graphics3D, rex, EvalEngine.get());
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

  // private static boolean exportGraphics2DOptions(ArrayNode arrayNode, IAST optionsList) {
  // for (int i = 1; i < optionsList.size(); i++) {
  // IExpr arg = optionsList.get(i);
  // if (arg.isRule()) {
  // IAST rule = (IAST) arg;
  // IExpr lhs = rule.arg1();
  // IExpr rhs = rule.arg2();
  // ObjectNode g = JSON_OBJECT_MAPPER.createObjectNode();
  // if (lhs == S.Axes) {
  // continue;
  // }
  // if (rhs.isSymbol()) {
  // if (rhs.isTrue()) {
  // g.put(lhs.toString(), true);
  // } else if (rhs.isFalse()) {
  // g.put(lhs.toString(), false);
  // } else {
  // g.put(lhs.toString(), rhs.toString());
  // }
  // } else {
  // g.set(lhs.toString(), ExpressionJSONConvert.exportExpressionJSON(rhs));
  // }
  // arrayNode.add(g);
  // } else {
  // return false;
  // }
  // }
  // return true;
  // }

  private static boolean graphics3DSingleLight(ObjectNode json, IAST result) {
    if (result.isAST1()) {
      IExpr color = result.arg1();
      if (color.isRGBColor()) {
        if (result.head().equals(S.AmbientLight)) {
          json.put("type", "ambient");
          GraphicsOptions.setColor(json, (IAST) color, F.RGBColor(F.C1, F.C1, F.C1), true);
          return true;
        }
      }
    } else if (result.isAST2()) {
      String name = result.arg1().toString();
      if (name.equals("Ambient")) {
        IExpr color = result.arg2();
        if (color.isRGBColor()) {
          json.put("type", "ambient");
          GraphicsOptions.setColor(json, (IAST) color, F.NIL, true);
          return true;
        }
      } else {
        IExpr color = result.arg1();
        if (color.isRGBColor() && result.arg2().isList()) {
          IAST list = (IAST) result.arg2();
          if (result.head().equals(S.DirectionalLight) && list.isList()) {
            json.put("type", "directional");
            GraphicsOptions.setColor(json, (IAST) color, F.NIL, true);
            graphics3DCoordsOrListOfCoords(json, list, "coords");
            return true;
          } else if (result.head().equals(S.PointLight) && list.isList3()) {
            json.put("type", "point");
            GraphicsOptions.setColor(json, (IAST) color, F.NIL, true);
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
              GraphicsOptions.setColor(json, (IAST) color, F.NIL, true);
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
          GraphicsOptions.setColor(json, (IAST) color, F.NIL, true);
          graphics3DCoordsOrListOfCoords(json, list, "coords");
          return true;
        } else if (name.equals("Point") && list.isList3()) {
          json.put("type", "point");
          GraphicsOptions.setColor(json, (IAST) color, F.NIL, true);
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
            GraphicsOptions.setColor(json, (IAST) color, F.NIL, true);
            graphics3DCoordsOrListOfCoords(json, coords, "coords");
            graphics3DCoordsOrListOfCoords(json, target, "target");
            return true;
          }
        }
      }
    }
    return false;

  }

  public static void initialize() {
    Initializer.init();
  }

  private static boolean primitivesDimension(IAST list, Dimensions2D dim) {
    for (int i = 1; i < list.size(); i++) {
      if (list.get(i).isAST()) {
        IAST primitive = (IAST) list.get(i);
        if (primitive.isBuiltInFunction()) {
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

  public static boolean renderGraphics2D(StringBuilder graphics2DBuffer, IAST graphics2DAST,
      EvalEngine engine) {
    return renderGraphics2D(graphics2DBuffer, graphics2DAST, true, false, engine);
  }

  public static boolean renderGraphics2D(StringBuilder graphics2DBuffer, IAST graphics2DAST,
      boolean javaScript, boolean prettyPrint, EvalEngine engine) {
    IAST arg1 = graphics2DAST.first().makeList();
    // IExpr lighting = S.Automatic;
    final OptionArgs options =
        new OptionArgs(graphics2DAST.topHead(), graphics2DAST, 2, engine, true);
    if (arg1.isBuiltInFunction()
        && graphics2DJSON(graphics2DBuffer, arg1, options, javaScript, prettyPrint)) {
      return true;
    }
    return false;
  }

  public static boolean renderGraphics3D(StringBuilder graphics3DBuffer, IAST graphics3DAST,
      EvalEngine engine) {
    return renderGraphics3D(graphics3DBuffer, graphics3DAST, true, engine);
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

    if (arg1.isBuiltInFunction()
        && graphics3DJSON(graphics3DBuffer, lighting, arg1, javaScript)) {
      return true;
    }
    return false;
  }

  private static void setOpacity(ObjectNode json, IExpr opacityExpr) {
    double opacity = opacityExpr.toDoubleDefault(1.0);
    json.put("opacity", opacity);
  }

  public static IAST textAtPoint(IExpr labeledPoint, IExpr x, IExpr y) {
    double xValue = x.evalf();
    // xValue += (GraphicsOptions.MEDIUM_FONTSIZE / 2);
    double yValue = y.evalf();
    return F.Text(labeledPoint.second(), F.List(F.num(xValue), F.num(yValue)));
  }

  private GraphicsFunctions() {}


}
