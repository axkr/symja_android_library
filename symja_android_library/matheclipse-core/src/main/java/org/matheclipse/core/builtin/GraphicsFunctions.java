package org.matheclipse.core.builtin;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.ExpressionJSONConvert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.IGraphics3D;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

public class GraphicsFunctions {
  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.Arrow.setEvaluator(new Arrow());
      S.BernsteinBasis.setEvaluator(new BernsteinBasis());
      S.Cuboid.setEvaluator(new Cuboid());
      S.Cylinder.setEvaluator(new Cylinder());
      S.Dodecahedron.setEvaluator(new Dodecahedron());
      S.Icosahedron.setEvaluator(new Icosahedron());
      S.Line.setEvaluator(new Line());
      S.Octahedron.setEvaluator(new Octahedron());
      S.Point.setEvaluator(new Point());
      S.Polygon.setEvaluator(new Polygon());
      S.Sphere.setEvaluator(new Sphere());
      S.Tetrahedron.setEvaluator(new Tetrahedron());

      S.Volume.setEvaluator(new Volume());
    }
  }

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
        setOpacity(buf, opacity);
        if (list.isListOfLists() && graphics3DCoords(buf, (IAST) list)) {
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
          return IOFunctions.printMessage(
              ast.topHead(), "invidx2", F.list(nArg2, F.C0, di), engine);
        }
        IExpr condition = F.Less(F.C0, x, F.C1);
        if (engine.evalTrue(condition)) {
          IInteger ni = F.ZZ(n);
          // Binomial(d, ni) * x^ni * (1 - x)^(di - ni)
          return F.Times(
              F.Binomial(di, ni), F.Power(x, ni), F.Power(F.Subtract(F.C1, x), F.Subtract(di, ni)));
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
      //      {
      //        type: 'cuboid',
      //        color: [0.5, 0.5, 0.5],
      //        coords: [
      //            [null, [0, 0, 0]],
      //            [[1, 1, 1]],
      //            [[1, 1, 1]],
      //            [[2, 2, 2]]
      //        ],
      //        opacity: 0.5
      //    }
      //      ArrayNode node = ExpressionJSONConvert.JSON_OBJECT_MAPPER.createArrayNode();
      //      ObjectNode object = ExpressionJSONConvert.JSON_OBJECT_MAPPER.createObjectNode();
      //      object.put("type", "cuboid");
      //      node.add(object);
      //      ArrayNode param = ExpressionJSONConvert.JSON_OBJECT_MAPPER.createArrayNode();
      //      param.add(0.5);
      //      param.add(0.5);
      //      param.add(0.5);
      //      object = ExpressionJSONConvert.JSON_OBJECT_MAPPER.createObjectNode();
      //      object.putPOJO("color", param);
      //      node.add(object);
      //
      //      param = ExpressionJSONConvert.JSON_OBJECT_MAPPER.createArrayNode();
      //      param.addNull();
      //      ArrayNode subParam = ExpressionJSONConvert.JSON_OBJECT_MAPPER.createArrayNode();
      //      subParam.add(0);
      //      subParam.add(0);
      //      subParam.add(0);
      //      param.addPOJO(subParam);
      //
      //      object = ExpressionJSONConvert.JSON_OBJECT_MAPPER.createObjectNode();
      //      object.putPOJO("coords", param);
      //      node.add(object);
      //
      //      object = ExpressionJSONConvert.JSON_OBJECT_MAPPER.createObjectNode();
      //      object.put("opacity", 0.5);
      //      node.add(object);
      //    return node;
      if (ast.argSize() > 0 && ast.arg1().isList()) {
        buf.append("{type: \'cuboid\',");
        setColor(buf, color, F.NIL, true);
        setOpacity(buf, opacity);
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

  private static class Cylinder extends AbstractEvaluator implements IGraphics3D {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST0()) {
        return F.Cylinder(F.List(F.List(0, 0, -1), F.List(0, 0, 1)));
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
        IAST list = (IAST) ast.arg1();
        buf.append("{type: \'cylinder\',");
        setColor(buf, color, F.NIL, true);
        setOpacity(buf, opacity);
        if (list.isListOfLists() && graphics3DCoords(buf, (IAST) list)) {
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
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_INFINITY;
    }

    @Override
    protected void addSubtypeThreejs(StringBuilder buf) {
      buf.append("subType: \'dodecahedron\',");
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
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
    protected void addSubtypeThreejs(StringBuilder buf) {
      buf.append("subType: \'icosahedron\',");
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class Line extends AbstractEvaluator implements IGraphics3D {

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
        buf.append("{type: \'line\',");
        setColor(buf, color, F.NIL, true);
        setOpacity(buf, opacity);
        if (list.isListOfLists() && graphics3DCoords(buf, (IAST) list)) {
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
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_INFINITY;
    }

    @Override
    protected void addSubtypeThreejs(StringBuilder buf) {
      buf.append("subType: \'octahedron\',");
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

    @Override
    public boolean graphics3D(StringBuilder buf, IAST ast, IAST color, IExpr opacity) {
      if (ast.argSize() > 0 && ast.arg1().isList()) {
        IAST list = (IAST) ast.arg1();
        buf.append("{type: \'point\',");
        setColor(buf, color, F.NIL, true);
        setOpacity(buf, opacity);
        if (list.isListOfLists() && graphics3DCoords(buf, (IAST) list)) {
          buf.append(",pointSize: 0.03}");
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
        setOpacity(buf, opacity);
        if (list.isListOfLists() && graphics3DCoords(buf, (IAST) list)) {
          buf.append("}");
          return true;
        }
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

    @Override
    public boolean graphics3D(StringBuilder buf, IAST ast, IAST color, IExpr opacity) {
      if (ast.argSize() > 0 && ast.arg1().isList()) {
        IAST list = (IAST) ast.arg1();
        buf.append("{type: \'sphere\',");
        setColor(buf, color, color, true);
        setOpacity(buf, opacity);
        buf.append("radius: 1,");
        if (list.isList3() && graphics3DCoords(buf, F.List(list))) {
          buf.append("}");
          return true;
        }
      }
      return false;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class Tetrahedron extends AbstractEvaluator implements IGraphics3D {

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
      setOpacity(buf, opacity);
      addSubtypeThreejs(buf);
      if (list.isListOfLists() && graphics3DCoords(buf, (IAST) list)) {
        buf.append("}");
        return true;
      }

      return false;
    }

    protected void addSubtypeThreejs(StringBuilder buf) {
      buf.append("subType: \'tetrahedron\',");
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
        } else if (graphic.isAST(S.Cuboid, 3)
            && graphic.first().isList3()
            && graphic.second().isList3()) {
          IAST v1 = (IAST) graphic.first();
          IAST v2 = (IAST) graphic.second();
          // Abs((-a + x)*(-b + y)*(-c + z))
          return F.Abs(
              F.Times( //
                  F.Plus(v1.arg1().negate(), v2.arg1()),
                  F.Plus(v1.arg2().negate(), v2.arg2()),
                  F.Plus(v1.arg3().negate(), v2.arg3())));
        } else if (graphic.isAST(S.Ellipsoid, 3)
            && graphic.first().isList3()
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

  private static void setColor(StringBuilder buf, IAST color, IAST defaultColor, boolean setComma) {
    if (color.isAST(S.RGBColor, 4)) {
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
    } else {
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
        buf.append("color: [0.0,0.0, 0.0]");
      }
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
      for (int j = 1; j < coords.size(); j++) {
        buf.append(coords.get(j).toString());
        if (j < coords.size() - 1) {
          buf.append(",");
        }
      }
      buf.append("]]");
      if (i < ast.size() - 1) {
        buf.append(",");
      }
    }
    buf.append("]");
    return true;
  }

  private static boolean graphics3DSingleCoords(StringBuilder buf, IAST coords, String coordStr) {
    buf.append(coordStr + ": ");
    buf.append("[[");
    for (int j = 1; j < coords.size(); j++) {
      buf.append(coords.get(j).toString());
      if (j < coords.size() - 1) {
        buf.append(",");
      }
    }
    buf.append("]]");
    return true;
  }

  public static boolean renderGraphics3D(
      StringBuilder graphics3DBuffer, IAST graphics3DAST, EvalEngine engine) {
    IExpr arg1 = graphics3DAST.first();
    if (!arg1.isList()) {
      arg1 = F.List(arg1);
    }
    IAST lighting = F.List(F.$str("Ambient"), F.RGBColor(F.C1, F.C1, F.C1));
    OptionArgs options = OptionArgs.createOptionArgs(graphics3DAST, engine);
    if (options != null) {
      IExpr option = options.getOption(S.Lighting);
      if (option.isList1()
          && option.first().isList()
          && option.first().size() > 2
          && option.first().first().isString()) {
        lighting = (IAST) option.first();
      }
    }
    IExpr data3D = engine.evaluate(F.N(arg1));
    if (data3D.isAST() && data3D.head().isBuiltInSymbol()) {
      StringBuilder jsonPrimitives = new StringBuilder();
      if (ExpressionJSONConvert.exportGraphics3D(jsonPrimitives, data3D)) {
        try {
          graphics3DBuffer.append("{");
          graphics3DBuffer.append("\naxes: {},");
          graphics3DBuffer.append("\nelements: [");
          graphics3DBuffer.append(jsonPrimitives.toString());
          graphics3DBuffer.append("],");
          boolean lightingDone = false;
          String name = lighting.arg1().toString();
          if (lighting.isList2()) {
            IExpr color = lighting.arg2();
            if (color.isAST(S.RGBColor, 4)) {
              if (name.equals("Ambient")) {
                graphics3DBuffer.append("\nlighting: [{");
                graphics3DBuffer.append("type: 'ambient',");
                setColor(graphics3DBuffer, (IAST) color, F.RGBColor(F.C1, F.C1, F.C1), false);
                graphics3DBuffer.append("}],");
                lightingDone = true;
              }
            }
          } else if (lighting.isAST(S.List, 4, 5)
              && lighting.arg3().isList()
              && lighting.size() > 2) {
            double angle = 1.0;
            if (lighting.size() == 5) {
              angle = lighting.arg4().toDoubleDefault(1.0);
            }
            IExpr color = lighting.arg2();
            IAST list = (IAST) lighting.arg3();
            if (color.isAST(S.RGBColor, 4)) {
              if (name.equals("Directional") && list.isList3()) {
                graphics3DBuffer.append("\nlighting: [{");
                graphics3DBuffer.append("type: 'directional',");
                setColor(graphics3DBuffer, (IAST) color, F.NIL, true);
                graphics3DSingleCoords(graphics3DBuffer, list, "coords");
                graphics3DBuffer.append("}],");
                lightingDone = true;
              } else if (name.equals("Point") && list.isList3()) {
                graphics3DBuffer.append("\nlighting: [{");
                graphics3DBuffer.append("type: 'point',");
                setColor(graphics3DBuffer, (IAST) color, F.NIL, true);
                graphics3DSingleCoords(graphics3DBuffer, list, "coords");
                graphics3DBuffer.append("}],");
                lightingDone = true;
              } else if (name.equals("Spot") && list.isList2() && list.isListOfLists()) {
                IAST coords = (IAST) list.arg1();
                IAST target = (IAST) list.arg2();
                if (coords.isList3() && target.isList3()) {
                  graphics3DBuffer.append("\nlighting: [{");
                  graphics3DBuffer.append("type: 'spot',");
                  graphics3DBuffer.append("angle: " + angle + ",");
                  setColor(graphics3DBuffer, (IAST) color, F.NIL, true);
                  graphics3DSingleCoords(graphics3DBuffer, coords, "coords");
                  graphics3DBuffer.append(",");
                  graphics3DSingleCoords(graphics3DBuffer, target, "target");
                  graphics3DBuffer.append("}],");
                  lightingDone = true;
                }
              }
            }
          }
          if (!lightingDone) {
            graphics3DBuffer.append("\nlighting: [{");
            graphics3DBuffer.append("type: 'ambient',");
            // white
            graphics3DBuffer.append("color: [1, 1, 1]");
            graphics3DBuffer.append("}");
            graphics3DBuffer.append("],");
          }
          graphics3DBuffer.append("\nviewpoint: [2,-4,4]");
          graphics3DBuffer.append("}");
          return true;
        } catch (Exception ex) {
          if (Config.SHOW_STACKTRACE) {
            ex.printStackTrace();
          }
        }
      }
    }
    return false;
  }

  public static void initialize() {
    Initializer.init();
  }

  private GraphicsFunctions() {}
}
