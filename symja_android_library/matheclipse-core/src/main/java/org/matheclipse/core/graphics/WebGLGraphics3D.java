package org.matheclipse.core.graphics;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class WebGLGraphics3D {

  private static final ObjectMapper mapper = new ObjectMapper();

  private interface Scaler {
    double scale(double val);

    String getName();
  }

  private static final Scaler SCALER_IDENTITY = new Scaler() {
    @Override
    public double scale(double v) {
      return v;
    }

    @Override
    public String getName() {
      return "Identity";
    }
  };
  private static final Scaler SCALER_LOG = new Scaler() {
    @Override
    public double scale(double v) {
      return (v > 0) ? Math.log10(v) : -300.0;
    }

    @Override
    public String getName() {
      return "Log";
    }
  };
  private static final Scaler SCALER_REVERSE = new Scaler() {
    @Override
    public double scale(double v) {
      return -v;
    }

    @Override
    public String getName() {
      return "Reverse";
    }
  };

  private static class ScalingContext {
    Scaler x = SCALER_IDENTITY;
    Scaler y = SCALER_IDENTITY;
    Scaler z = SCALER_IDENTITY;

    ScalingContext(Scaler[] s) {
      if (s.length > 0)
        x = s[0];
      if (s.length > 1)
        y = s[1];
      if (s.length > 2)
        z = s[2];
    }
  }

  private static class LightConfig {
    String type;
    int color;
    double intensity = 1.0;
    double[] position;
    double[] target;
    double angle;
    double decay;
    double distance;
    boolean fixedToCamera = false;

    public LightConfig(String type, int color) {
      this.type = type;
      this.color = color;
    }
  }

  private static class GraphicsState implements Cloneable {
    Color color = new Color(1.0f, 0.5f, 0.0f);
    double opacity = 1.0;
    boolean dashed = false;
    double thickness = 1.0;
    boolean showMesh = true;

    @Override
    public GraphicsState clone() {
      try {
        return (GraphicsState) super.clone();
      } catch (CloneNotSupportedException e) {
        GraphicsState c = new GraphicsState();
        c.color = this.color;
        c.opacity = this.opacity;
        c.dashed = this.dashed;
        c.thickness = this.thickness;
        c.showMesh = this.showMesh;
        return c;
      }
    }
  }

  private static class ComplexContext {
    final IAST points;
    final IAST vertexColors;
    final IAST vertexNormals;

    public ComplexContext(IAST points, IAST vertexColors, IAST vertexNormals) {
      this.points = points;
      this.vertexColors = vertexColors;
      this.vertexNormals = vertexNormals;
    }

    public double[] resolve(IExpr expr, double[] def) {
      if (expr.isList()) {
        return getVector(expr, def);
      }
      if (expr.isInteger() && points != null) {
        int idx = expr.toIntDefault(0);
        if (idx > 0 && idx < points.size()) {
          return getVector(points.get(idx), def);
        }
      }
      return def;
    }
  }

  public static String generateHTMLSnippet(IAST graphics) {
    return generateOutput(graphics, true);
  }

  public static String generateHTML(IAST graphics) {
    return generateOutput(graphics, false);
  }

  public static String generateHTML(IAST graphics, boolean isSnippet) {
    return generateOutput(graphics, isSnippet);
  }

  private static String generateOutput(IAST graphics, boolean isSnippet) {
    try {
      ObjectNode rootNode = mapper.createObjectNode();
      ArrayNode elementsArray = rootNode.putArray("elements");

      boolean showLegend = false;
      String legendText = null;
      if (graphics.isAST(S.Legended)) {
        showLegend = true;
        if (graphics.size() > 2 && !graphics.arg2().equals(S.Automatic))
          legendText = graphics.arg2().toString();
        IExpr content = graphics.arg1();
        if (content.isAST())
          graphics = (IAST) content;
      }

      if (showLegend) {
        rootNode.put("showLegend", true);
        if (legendText != null)
          rootNode.put("legendText", legendText);
      }

      Scaler[] scalers = parseScaling(graphics);
      ScalingContext scalingCtx = new ScalingContext(scalers);

      ArrayNode scalingNode = rootNode.putArray("scaling");
      scalingNode.add(scalers[0].getName());
      scalingNode.add(scalers[1].getName());
      scalingNode.add(scalers[2].getName());

      IExpr boxRatiosOpt = extractOption(graphics, S.BoxRatios);
      if (boxRatiosOpt != null && boxRatiosOpt.isList() && ((IAST) boxRatiosOpt).size() >= 4) {
        ArrayNode br = rootNode.putArray("boxRatios");
        br.add(getDouble(((IAST) boxRatiosOpt).get(1)));
        br.add(getDouble(((IAST) boxRatiosOpt).get(2)));
        br.add(getDouble(((IAST) boxRatiosOpt).get(3)));
      }

      parseAxesOption(rootNode, graphics);

      // Lights
      List<LightConfig> lights = new ArrayList<>();
      if (graphics.isAST(S.Graphics3D) || graphics.isAST(S.SurfaceGraphics))
        parseLightingOption(graphics, lights);
      if (lights.isEmpty())
        addDefaultLighting(lights);
      ArrayNode lightsArray = rootNode.putArray("lights");
      for (LightConfig l : lights) {
        ObjectNode lNode = lightsArray.addObject();
        lNode.put("type", l.type);
        lNode.put("color", l.color);
        lNode.put("intensity", l.intensity);
        lNode.put("fixedToCamera", l.fixedToCamera);
        if (l.position != null)
          lNode.set("position", vecToJsonArray(l.position));
        if (l.target != null)
          lNode.set("target", vecToJsonArray(l.target));
        lNode.put("angle", l.angle);
        lNode.put("distance", l.distance);
      }

      // Process Geometry
      if (graphics.isAST(S.SurfaceGraphics)) {
        processSurfaceGraphics(elementsArray, graphics, new GraphicsState(), scalingCtx);
      } else if (graphics.isAST(S.Graphics3D) && graphics.argSize() >= 1) {
        processExpr(elementsArray, graphics.arg1(), new GraphicsState(), null, scalingCtx);
      } else if (graphics.argSize() >= 1) {
        processExpr(elementsArray, graphics.arg1(), new GraphicsState(), null, scalingCtx);
      }

      String jsonData = mapper.writeValueAsString(rootNode);

      if (isSnippet) {
        return createSnippetHTML(jsonData);
      } else {
        return getHTMLTemplate(jsonData);
      }

    } catch (IOException e) {
      e.printStackTrace();
      return isSnippet ? "Error generating WebGL" : "";
    }
  }

  private static void parseAxesOption(ObjectNode root, IAST graphics) {
    IExpr axesOpt = extractOption(graphics, S.Axes);
    boolean[] axes = {true, true, true};
    if (axesOpt != null) {
      if (axesOpt.isFalse()) {
        axes[0] = axes[1] = axes[2] = false;
      } else if (axesOpt.isList() && ((IAST) axesOpt).size() >= 4) {
        axes[0] = !((IAST) axesOpt).get(1).isFalse();
        axes[1] = !((IAST) axesOpt).get(2).isFalse();
        axes[2] = !((IAST) axesOpt).get(3).isFalse();
      }
    }
    ArrayNode axesNode = root.putArray("axes");
    axesNode.add(axes[0]).add(axes[1]).add(axes[2]);
  }

  private static Scaler[] parseScaling(IAST graphics) {
    Scaler[] result = {SCALER_IDENTITY, SCALER_IDENTITY, SCALER_IDENTITY};
    IExpr opt = extractOption(graphics, S.ScalingFunctions);
    if (opt != null) {
      if (opt.isList() && ((IAST) opt).size() >= 4) {
        IAST list = (IAST) opt;
        result[0] = getScaler(list.get(1));
        result[1] = getScaler(list.get(2));
        result[2] = getScaler(list.get(3));
      } else if (opt.isString()) {
        if ("Log".equalsIgnoreCase(opt.toString())) {
          result[2] = SCALER_LOG;
        }
      }
    }
    return result;
  }

  private static Scaler getScaler(IExpr expr) {
    if (expr.isString()) {
      String s = expr.toString();
      if ("Log".equalsIgnoreCase(s))
        return SCALER_LOG;
      if ("Reverse".equalsIgnoreCase(s))
        return SCALER_REVERSE;
    }
    return SCALER_IDENTITY;
  }

  private static void parseLightingOption(IAST graphics, List<LightConfig> lights) {
    for (int i = 1; i < graphics.size(); i++) {
      IExpr arg = graphics.get(i);
      if (arg.isRuleAST()) {
        IAST rule = (IAST) arg;
        if (rule.arg1().equals(S.Lighting)) {
          processLightingValue(rule.arg2(), lights);
        }
      }
    }
  }

  private static void processLightingValue(IExpr value, List<LightConfig> lights) {
    if (value.equals(S.None))
      return;
    if (value.equals(S.Automatic)) {
      addDefaultLighting(lights);
      return;
    }
    if (value.isString()) {
      String name = value.toString();
      if ("Neutral".equalsIgnoreCase(name))
        addNeutralLighting(lights);
      else if ("Standard".equalsIgnoreCase(name))
        addStandardLighting(lights);
      else
        addDefaultLighting(lights);
      return;
    }
    if (value.isList()) {
      IAST list = (IAST) value;
      for (int i = 1; i < list.size(); i++) {
        IExpr lightSpec = list.get(i);
        if (lightSpec.equals(S.None))
          continue;
        if (lightSpec.isAST()) {
          parseLightPrimitive((IAST) lightSpec, lights);
        }
      }
    }
  }

  private static void parseLightPrimitive(IAST spec, List<LightConfig> lights) {
    ISymbol head = spec.topHead();
    String type = head.toString();
    Color col = (spec.argSize() >= 1) ? parseRawColor(spec.arg1()) : Color.WHITE;
    int colInt = col.getRGB() & 0x00FFFFFF;

    if ("AmbientLight".equals(type)) {
      lights.add(new LightConfig("AmbientLight", colInt));
    } else if ("DirectionalLight".equals(type)) {
      LightConfig l = new LightConfig("DirectionalLight", colInt);
      if (spec.argSize() >= 2)
        l.position = getVector(spec.arg2(), new double[] {1, 1, 1});
      else
        l.position = new double[] {10, 10, 10};
      lights.add(l);
    } else if ("PointLight".equals(type)) {
      LightConfig l = new LightConfig("PointLight", colInt);
      if (spec.argSize() >= 2)
        l.position = getVector(spec.arg2(), new double[] {0, 0, 0});
      if (spec.argSize() >= 3)
        l.distance = getDouble(spec.arg3(), 0.0);
      lights.add(l);
    } else if ("SpotLight".equals(type)) {
      LightConfig l = new LightConfig("SpotLight", colInt);
      if (spec.argSize() >= 2)
        l.position = getVector(spec.arg2(), new double[] {0, 0, 10});
      else
        l.position = new double[] {0, 0, 10};
      double[] dir = new double[] {0, 0, -1};
      if (spec.argSize() >= 3)
        dir = getVector(spec.arg3(), dir);
      l.target =
          new double[] {l.position[0] + dir[0], l.position[1] + dir[1], l.position[2] + dir[2]};
      if (spec.argSize() >= 4)
        l.angle = getDouble(spec.arg4(), Math.PI / 4);
      else
        l.angle = Math.PI / 3;
      lights.add(l);
    }
  }

  private static void addDefaultLighting(List<LightConfig> lights) {
    lights.add(new LightConfig("AmbientLight", 0x505050));
    double[][] positions = {{10, 10, 10}, {-10, 10, 10}, {10, -10, 10}, {-10, -10, 10}};
    for (double[] pos : positions) {
      LightConfig l = new LightConfig("DirectionalLight", 0xffffff);
      l.intensity = 0.4;
      l.position = pos;
      l.fixedToCamera = true;
      lights.add(l);
    }
  }

  private static void addNeutralLighting(List<LightConfig> lights) {
    lights.add(new LightConfig("AmbientLight", 0x404040));
    double[][] dirs = {{1, 1, 1}, {-1, -1, 1}, {-1, 1, 1}, {1, -1, 1}};
    for (double[] d : dirs) {
      LightConfig dl = new LightConfig("DirectionalLight", 0xffffff);
      dl.intensity = 0.5;
      dl.position = d;
      lights.add(dl);
    }
  }

  private static void addStandardLighting(List<LightConfig> lights) {
    lights.add(new LightConfig("AmbientLight", 0x202020));
    LightConfig red = new LightConfig("DirectionalLight", 0xff0000);
    red.position = new double[] {1, 0, 0};
    lights.add(red);
    LightConfig green = new LightConfig("DirectionalLight", 0x00ff00);
    green.position = new double[] {0, 1, 0};
    lights.add(green);
    LightConfig blue = new LightConfig("DirectionalLight", 0x0000ff);
    blue.position = new double[] {0, 0, 1};
    lights.add(blue);
  }

  private static double[] getVector(IExpr expr, double[] def) {
    if (expr.isList()) {
      IAST list = (IAST) expr;
      if (list.size() >= 4)
        return new double[] {getDouble(list.get(1)), getDouble(list.get(2)),
            getDouble(list.get(3))};
    }
    return def;
  }

  private static ArrayNode vecToJsonArray(double[] v) {
    ArrayNode n = mapper.createArrayNode();
    n.add(v[0]).add(v[1]).add(v[2]);
    return n;
  }

  private static void processExpr(ArrayNode array, IExpr expr, GraphicsState state,
      ComplexContext context, ScalingContext scaling) {
    if (expr.isList()) {
      GraphicsState scopedState = state.clone();
      IAST list = (IAST) expr;
      for (int i = 1; i < list.size(); i++) {
        processItem(array, list.get(i), scopedState, context, scaling);
      }
    } else {
      processItem(array, expr, state, context, scaling);
    }
  }

  private static void processItem(ArrayNode array, IExpr expr, GraphicsState state,
      ComplexContext context, ScalingContext scaling) {
    if (expr.isBuiltInSymbol()) {
      processSymbol((IBuiltInSymbol) expr, state);
      return;
    }
    if (!expr.isAST())
      return;

    IAST ast = (IAST) expr;
    IExpr headExpr = ast.head();

    if (headExpr.isBuiltInSymbol()) {
      IBuiltInSymbol head = (IBuiltInSymbol) headExpr;
      int id = head.ordinal();

      switch (id) {
        case ID.List:
        case ID.GraphicsGroup:
          processExpr(array, ast, state, context, scaling);
          break;
        case ID.RGBColor:
        case ID.Hue:
        case ID.GrayLevel:
        case ID.CMYKColor:
          state.color = parseColor(ast);
          break;
        case ID.Opacity:
          state.opacity = getDouble(ast.arg1(), 1.0);
          break;
        case ID.Thickness:
        case ID.AbsoluteThickness:
          state.thickness = getDouble(ast.arg1(), 1.0);
          break;
        case ID.Dashed:
          state.dashed = true;
          break;
        case ID.SurfaceGraphics:
          processSurfaceGraphics(array, ast, state, scaling);
          break;
        case ID.GraphicsComplex:
          if (ast.argSize() >= 2) {
            IExpr ptsExpr = ast.arg1();
            IExpr primitives = ast.arg2();
            IAST pts = ptsExpr.isList() ? (IAST) ptsExpr : null;
            IAST vColors = extractOptionList(ast, S.VertexColors);
            IAST vNormals = extractOptionList(ast, S.VertexNormals);
            ComplexContext newContext = new ComplexContext(pts, vColors, vNormals);
            processExpr(array, primitives, state, newContext, scaling);
          }
          break;
        case ID.Line:
          createPolyNode(array, "Line", ast.arg1(), state, context,
              extractOptionList(ast, S.VertexColors), null, scaling);
          break;
        case ID.Polygon:
          createPolyNode(array, "Polygon", ast.arg1(), state, context,
              extractOptionList(ast, S.VertexColors), extractOptionList(ast, S.VertexNormals),
              scaling);
          break;
        case ID.Point:
          createPolyNode(array, "Point", ast.arg1(), state, context,
              extractOptionList(ast, S.VertexColors), null, scaling);
          break;
        case ID.Sphere: {
          ObjectNode node = createBaseNode(array, "Sphere", state);
          double[] center =
              resolveAndScaleVector(ast.arg1(), context, scaling, new double[] {0, 0, 0});
          node.set("center", vecToJsonArray(center));
          node.put("radius", (ast.argSize() >= 2) ? getDouble(ast.arg2(), 1.0) : 1.0);
        }
          break;
        case ID.Cone: {
          ObjectNode node = createBaseNode(array, "Cone", state);
          if (ast.argSize() == 0) {
            node.set("start", vecToJsonArray(applyScaling(new double[] {0, 0, -1}, scaling)));
            node.set("end", vecToJsonArray(applyScaling(new double[] {0, 0, 1}, scaling)));
            node.put("radius", 1.0);
          } else {
            IExpr coords = ast.arg1();
            double radius = (ast.argSize() >= 2) ? getDouble(ast.arg2(), 1.0) : 1.0;
            node.put("radius", radius);
            if (coords.isList() && ((IAST) coords).size() > 2) {
              node.set("start", vecToJsonArray(resolveAndScaleVector(((IAST) coords).get(1),
                  context, scaling, new double[] {0, 0, 0})));
              node.set("end", vecToJsonArray(resolveAndScaleVector(((IAST) coords).get(2), context,
                  scaling, new double[] {0, 0, 1})));
            } else {
              node.set("start", vecToJsonArray(applyScaling(new double[] {0, 0, 0}, scaling)));
              node.set("end", vecToJsonArray(applyScaling(new double[] {0, 0, 1}, scaling)));
            }
          }
        }
          break;
        case ID.Tube: {
          ObjectNode node = createBaseNode(array, "Tube", state);
          if (ast.arg1().isAST()) {
            IAST geometry = (IAST) ast.arg1();
            double radius = (ast.argSize() >= 2) ? getDouble(ast.arg2(), 0.1) : 0.1;
            node.put("radius", radius);
            if (geometry.isAST(S.BSplineCurve)) {
              node.put("pathType", "BSpline");
              processBSplineData(node, geometry, scaling);
            } else {
              node.put("pathType", "CatmullRom");
              IAST pointsList = null;
              if (geometry.isList()) {
                pointsList = geometry;
              } else if (geometry.isASTSizeGE(S.Line, 2)) {
                pointsList = (IAST) geometry.first();
              }
              ArrayNode pointsJson = node.putArray("points");
              if (pointsList != null) {
                for (int i = 1; i < pointsList.size(); i++) {
                  double[] pt = resolveAndScaleVector(pointsList.get(i), context, scaling, null);
                  if (pt != null) {
                    pointsJson.add(pt[0]).add(pt[1]).add(pt[2]);
                  }
                }
              }
            }
          }
        }
          break;
        case ID.BSplineCurve: {
          ObjectNode node = createBaseNode(array, "BSplineCurve", state);
          processBSplineData(node, ast, scaling);
        }
          break;
        case ID.Cuboid: {
          ObjectNode node = createBaseNode(array, "Cuboid", state);
          IExpr minE = ast.arg1();
          IExpr maxE = (ast.argSize() >= 2) ? ast.arg2() : F.List(F.num(1), F.num(1), F.num(1));
          double[] min = resolveAndScaleVector(minE, context, scaling, new double[] {0, 0, 0});
          double[] max = resolveAndScaleVector(maxE, context, scaling, new double[] {1, 1, 1});
          double x1 = Math.min(min[0], max[0]), x2 = Math.max(min[0], max[0]);
          double y1 = Math.min(min[1], max[1]), y2 = Math.max(min[1], max[1]);
          double z1 = Math.min(min[2], max[2]), z2 = Math.max(min[2], max[2]);
          node.set("min", vecToJsonArray(new double[] {x1, y1, z1}));
          node.set("max", vecToJsonArray(new double[] {x2, y2, z2}));
        }
          break;
        case ID.Cylinder: {
          ObjectNode node = createBaseNode(array, "Cylinder", state);
          IExpr coords = ast.arg1();
          double radius = (ast.argSize() >= 2) ? getDouble(ast.arg2(), 1.0) : 1.0;
          node.put("radius", radius);
          if (coords.isList() && ((IAST) coords).size() > 2) {
            node.set("start", vecToJsonArray(resolveAndScaleVector(((IAST) coords).get(1), context,
                scaling, new double[] {0, 0, -1})));
            node.set("end", vecToJsonArray(resolveAndScaleVector(((IAST) coords).get(2), context,
                scaling, new double[] {0, 0, 1})));
          } else {
            node.set("start", vecToJsonArray(applyScaling(new double[] {0, 0, -1}, scaling)));
            node.set("end", vecToJsonArray(applyScaling(new double[] {0, 0, 1}, scaling)));
          }
        }
          break;
      }
    }
  }

  private static double[] resolveAndScaleVector(IExpr expr, ComplexContext context,
      ScalingContext scaling, double[] def) {
    double[] v = def;
    if (context != null) {
      v = context.resolve(expr, def);
    } else {
      v = getVector(expr, def);
    }
    if (v == null)
      return null;
    return applyScaling(v, scaling);
  }

  private static double[] applyScaling(double[] v, ScalingContext scaling) {
    if (v.length < 3)
      return v;
    v[0] = scaling.x.scale(v[0]);
    v[1] = scaling.y.scale(v[1]);
    v[2] = scaling.z.scale(v[2]);
    return v;
  }

  private static void processBSplineData(ObjectNode node, IAST bsplineAST, ScalingContext scaling) {
    IExpr pointsExpr = bsplineAST.arg1();
    ArrayNode pointsJson = node.putArray("points");
    if (pointsExpr.isList()) {
      IAST pList = (IAST) pointsExpr;
      for (int i = 1; i < pList.size(); i++) {
        IExpr pt = pList.get(i);
        if (pt.isList()) {
          double[] scaled = resolveAndScaleVector(pt, null, scaling, new double[] {0, 0, 0});
          pointsJson.add(scaled[0]).add(scaled[1]).add(scaled[2]);
        }
      }
    }
    int degree = 3;
    boolean closed = false;
    double[] weights = null;
    double[] knots = null;
    IExpr optDegree = extractOption(bsplineAST, S.SplineDegree);
    if (optDegree != null)
      degree = optDegree.toIntDefault(3);
    IExpr optClosed = extractOption(bsplineAST, S.SplineClosed);
    if (optClosed != null && optClosed.isTrue())
      closed = true;
    IExpr optWeights = extractOption(bsplineAST, S.SplineWeights);
    if (optWeights != null && optWeights.isList()) {
      IAST wList = (IAST) optWeights;
      weights = new double[pointsJson.size() / 3];
      for (int i = 0; i < weights.length && i < wList.size() - 1; i++) {
        weights[i] = getDouble(wList.get(i + 1), 1.0);
      }
    }
    IExpr optKnots = extractOption(bsplineAST, S.SplineKnots);
    if (optKnots != null && optKnots.isList()) {
      IAST kList = (IAST) optKnots;
      knots = new double[kList.size() - 1];
      for (int i = 1; i < kList.size(); i++)
        knots[i - 1] = getDouble(kList.get(i));
    }
    int nPoints = pointsJson.size() / 3;
    if (closed) {
      for (int i = 0; i < degree; i++) {
        pointsJson.add(pointsJson.get(i * 3));
        pointsJson.add(pointsJson.get(i * 3 + 1));
        pointsJson.add(pointsJson.get(i * 3 + 2));
      }
      if (knots == null) {
        int n = nPoints + degree;
        knots = new double[n + degree + 1];
        for (int i = 0; i < knots.length; i++)
          knots[i] = i;
      }
    } else {
      if (knots == null) {
        int n = nPoints;
        knots = new double[n + degree + 1];
        for (int i = 0; i <= degree; i++)
          knots[i] = 0.0;
        int internal = n - degree;
        for (int i = 1; i < internal; i++)
          knots[degree + i] = (double) i / internal;
        for (int i = n; i < knots.length; i++)
          knots[i] = 1.0;
      }
    }
    node.put("degree", degree);
    node.put("closed", closed);
    ArrayNode kJson = node.putArray("knots");
    if (knots != null)
      for (double k : knots)
        kJson.add(k);
    if (weights != null) {
      ArrayNode wJson = node.putArray("weights");
      for (double w : weights)
        wJson.add(w);
    }
  }

  private static IAST extractOptionList(IAST ast, ISymbol optionName) {
    IExpr res = extractOption(ast, optionName);
    return (res != null && res.isList()) ? (IAST) res : null;
  }

  private static IExpr extractOption(IAST ast, ISymbol optionName) {
    for (int i = 2; i < ast.size(); i++) {
      if (ast.get(i).isRuleAST()) {
        IAST rule = (IAST) ast.get(i);
        if (rule.arg1().equals(optionName))
          return rule.arg2();
      }
    }
    return null;
  }

  private static void createPolyNode(ArrayNode array, String type, IExpr data, GraphicsState state,
      ComplexContext context, IAST localVertexColors, IAST localVertexNormals,
      ScalingContext scaling) {
    ObjectNode node = createBaseNode(array, type, state);
    ArrayNode pointsJson = node.putArray("points");
    List<IExpr> allVertices = new ArrayList<>();
    List<Color> allColors = new ArrayList<>();
    List<IExpr> allNormals = new ArrayList<>();

    if (data.isList()) {
      IAST listData = (IAST) data;
      if (listData.size() > 1) {
        IExpr first = listData.get(1);
        boolean isMulti = false;
        if (context != null) {
          if (first.isList())
            isMulti = true;
        } else {
          if (first.isList()) {
            IAST firstList = (IAST) first;
            if (firstList.size() > 1 && firstList.get(1).isList())
              isMulti = true;
          }
        }
        if (isMulti) {
          for (int i = 1; i < listData.size(); i++)
            processSingleFace(listData.get(i), type, context, localVertexColors, localVertexNormals,
                i - 1, allVertices, allColors, allNormals);
        } else {
          processSingleFace(data, type, context, localVertexColors, localVertexNormals, 0,
              allVertices, allColors, allNormals);
        }
      }
    }
    for (IExpr v : allVertices) {
      if (v.isList()) {
        IAST vList = (IAST) v;
        if (vList.size() >= 4) {
          double x = getDouble(vList.get(1));
          double y = getDouble(vList.get(2));
          double z = getDouble(vList.get(3));
          pointsJson.add(scaling.x.scale(x));
          pointsJson.add(scaling.y.scale(y));
          pointsJson.add(scaling.z.scale(z));
        }
      }
    }
    if (!allColors.isEmpty() && allColors.size() == allVertices.size()) {
      node.put("color", 0xFFFFFF);
      ArrayNode colorsJson = node.putArray("vertexColors");
      for (Color c : allColors) {
        colorsJson.add(c.getRed() / 255.0).add(c.getGreen() / 255.0).add(c.getBlue() / 255.0);
      }
    }
    if (!allNormals.isEmpty() && allNormals.size() == allVertices.size()) {
      ArrayNode normalsJson = node.putArray("vertexNormals");
      for (IExpr n : allNormals) {
        if (n.isList() && ((IAST) n).size() >= 4) {
          normalsJson.add(getDouble(((IAST) n).get(1)));
          normalsJson.add(getDouble(((IAST) n).get(2)));
          normalsJson.add(getDouble(((IAST) n).get(3)));
        } else {
          normalsJson.add(0).add(0).add(1);
        }
      }
    }
  }

  private static void processSingleFace(IExpr faceData, String type, ComplexContext context,
      IAST localVertexColors, IAST localVertexNormals, int faceIndex, List<IExpr> outVertices,
      List<Color> outColors, List<IExpr> outNormals) {
    List<IExpr> faceVerts = new ArrayList<>();
    List<Color> faceCols = new ArrayList<>();
    List<IExpr> faceNorms = new ArrayList<>();

    resolveComplexAttributes(faceData, context, localVertexColors, localVertexNormals, 0, faceVerts,
        faceCols, faceNorms);

    if (faceVerts.isEmpty())
      return;
    if (type.equals("Line") || type.equals("Point")) {
      outVertices.addAll(faceVerts);
      outColors.addAll(faceCols);
      outNormals.addAll(faceNorms);
      return;
    }
    if (faceVerts.size() >= 3) {
      IExpr v0 = faceVerts.get(0);
      Color c0 = !faceCols.isEmpty() ? faceCols.get(0) : null;
      IExpr n0 = !faceNorms.isEmpty() ? faceNorms.get(0) : null;

      for (int i = 1; i < faceVerts.size() - 1; i++) {
        outVertices.add(v0);
        outVertices.add(faceVerts.get(i));
        outVertices.add(faceVerts.get(i + 1));
        if (c0 != null) {
          outColors.add(c0);
          outColors.add(faceCols.get(i));
          outColors.add(faceCols.get(i + 1));
        }
        if (n0 != null) {
          outNormals.add(n0);
          outNormals.add(faceNorms.get(i));
          outNormals.add(faceNorms.get(i + 1));
        }
      }
    }
  }

  private static int resolveComplexAttributes(IExpr data, ComplexContext context,
      IAST localColorList, IAST localNormalList, int localIndex, List<IExpr> outPoints,
      List<Color> outColors, List<IExpr> outNormals) {
    if (!data.isList())
      return localIndex;
    IAST list = (IAST) data;
    for (int i = 1; i < list.size(); i++) {
      IExpr el = list.get(i);
      if (context != null && el.isInteger()) {
        int idx = el.toIntDefault(0);
        if (context.points != null && idx > 0 && idx < context.points.size()) {
          outPoints.add(context.points.get(idx));
        } else {
          outPoints.add(F.List(F.num(0), F.num(0), F.num(0)));
        }

        if (localColorList != null && localIndex < localColorList.size() - 1) {
          outColors.add(parseRawColor(localColorList.get(localIndex + 1)));
        } else if (context.vertexColors != null && idx > 0 && idx < context.vertexColors.size()) {
          outColors.add(parseRawColor(context.vertexColors.get(idx)));
        } else if (context.vertexColors != null) {
          outColors.add(Color.WHITE);
        }

        if (localNormalList != null && localIndex < localNormalList.size() - 1) {
          outNormals.add(localNormalList.get(localIndex + 1));
        } else if (context.vertexNormals != null && idx > 0 && idx < context.vertexNormals.size()) {
          outNormals.add(context.vertexNormals.get(idx));
        }

      } else {
        outPoints.add(el);
        if (localColorList != null && localIndex < localColorList.size() - 1) {
          outColors.add(parseRawColor(localColorList.get(localIndex + 1)));
        }
        if (localNormalList != null && localIndex < localNormalList.size() - 1) {
          outNormals.add(localNormalList.get(localIndex + 1));
        }
      }
      localIndex++;
    }
    return localIndex;
  }

  private static ObjectNode createBaseNode(ArrayNode array, String type, GraphicsState state) {
    ObjectNode node = array.addObject();
    node.put("type", type);
    node.put("color", state.color.getRGB() & 0x00FFFFFF);
    node.put("opacity", state.opacity);
    if (state.dashed)
      node.put("dashed", true);
    return node;
  }

  private static double getDouble(IExpr expr, double def) {
    try {
      if (expr instanceof INumber)
        return ((INumber) expr).reDoubleValue();
      return expr.evalf();
    } catch (RuntimeException e) {
      return def;
    }
  }

  private static double getDouble(IExpr expr) {
    return getDouble(expr, 0.0);
  }

  private static float clamp(double val) {
    return (float) Math.max(0.0, Math.min(1.0, val));
  }

  private static void processSymbol(IBuiltInSymbol sym, GraphicsState state) {
    switch (sym.ordinal()) {
      case ID.Red:
        state.color = Color.RED;
        break;
      case ID.Green:
        state.color = Color.GREEN;
        break;
      case ID.Blue:
        state.color = Color.BLUE;
        break;
      case ID.Black:
        state.color = Color.BLACK;
        break;
      case ID.White:
        state.color = Color.WHITE;
        break;
      case ID.Gray:
        state.color = Color.GRAY;
        break;
      case ID.Yellow:
        state.color = Color.YELLOW;
        break;
      case ID.Cyan:
        state.color = Color.CYAN;
        break;
      case ID.Magenta:
        state.color = Color.MAGENTA;
        break;
      case ID.Orange:
        state.color = Color.ORANGE;
        break;
      case ID.Brown:
        state.color = new Color(165, 42, 42);
        break;
      case ID.Dashed:
        state.dashed = true;
        break;
      case ID.Thick:
        state.thickness = 2.0;
        break;
    }
  }

  private static Color parseRawColor(IExpr expr) {
    if (expr instanceof INumber) {
      float g = (float) ((INumber) expr).reDoubleValue();
      return new Color(clamp(g), clamp(g), clamp(g));
    }
    if (expr.isList()) {
      IAST list = (IAST) expr;
      if (list.size() >= 4)
        return new Color(clamp(getDouble(list.get(1))), clamp(getDouble(list.get(2))),
            clamp(getDouble(list.get(3))));
      else if (list.size() == 2) {
        float g = (float) getDouble(list.get(1));
        return new Color(clamp(g), clamp(g), clamp(g));
      }
    }
    if (expr.isAST())
      return parseColor((IAST) expr);
    return Color.WHITE;
  }

  private static Color parseColor(IAST ast) {
    ISymbol head = ast.topHead();
    if (head.isBuiltInSymbol()) {
      switch (head.ordinal()) {
        case ID.RGBColor:
          return new Color(clamp(getDouble(ast.arg1())), clamp(getDouble(ast.arg2())),
              clamp(getDouble(ast.arg3())));
        case ID.Hue:
          return Color.getHSBColor((float) getDouble(ast.arg1()),
              (ast.argSize() >= 2) ? (float) getDouble(ast.arg2()) : 1f,
              (ast.argSize() >= 3) ? (float) getDouble(ast.arg3()) : 1f);
        case ID.GrayLevel:
          float g = (float) getDouble(ast.arg1());
          return new Color(g, g, g);
        case ID.CMYKColor:
          float c = (float) getDouble(ast.arg1()), m = (float) getDouble(ast.arg2()),
              y = (float) getDouble(ast.arg3()), k = (float) getDouble(ast.arg4());
          return new Color(clamp((1 - c) * (1 - k)), clamp((1 - m) * (1 - k)),
              clamp((1 - y) * (1 - k)));
      }
    }
    return Color.BLACK;
  }

  private static void processSurfaceGraphics(ArrayNode array, IAST ast, GraphicsState state,
      ScalingContext scaling) {
    IExpr zArg = ast.arg1();
    if (!zArg.isList())
      return;
    IAST zRows = (IAST) zArg;
    ObjectNode node = createBaseNode(array, "GridSurface", state);
    ArrayNode zValues = node.putArray("zData");
    ArrayNode cValues = node.putArray("colorData");
    int rows = zRows.size() - 1;
    int cols = 0;
    for (int i = 1; i < zRows.size(); i++) {
      IExpr row = zRows.get(i);
      if (row.isList()) {
        IAST rowList = (IAST) row;
        if (cols == 0)
          cols = rowList.size() - 1;
        for (int j = 1; j < rowList.size(); j++) {
          zValues.add(scaling.z.scale(getDouble(rowList.get(j))));
        }
      }
    }
    node.put("rows", rows);
    node.put("cols", cols);
    double xMin = 1, xMax = Math.max(1, cols);
    double yMin = 1, yMax = Math.max(1, rows);
    boolean mesh = true;
    for (int i = 2; i < ast.size(); i++) {
      if (ast.get(i).isRuleAST()) {
        IAST rule = (IAST) ast.get(i);
        String key = rule.arg1().toString();
        if ("MeshRange".equals(key)) {
          // ... range parsing ...
        } else if ("Mesh".equals(key)) {
          mesh = rule.arg2().isTrue();
        }
      }
    }
    node.put("xMin", scaling.x.scale(xMin)).put("xMax", scaling.x.scale(xMax));
    node.put("yMin", scaling.y.scale(yMin)).put("yMax", scaling.y.scale(yMax));
    node.put("showMesh", mesh);
  }

  // --- JS Generation Methods ---

  private static String createSnippetHTML(String jsonData) {
    String containerId =
        "webgl_" + System.currentTimeMillis() + "_" + (int) (Math.random() * 10000);
    StringBuilder html = new StringBuilder();
    html.append("<div data-type=\"webgl\" id=\"").append(containerId).append(
        "\" style=\"width: 500px; height: 400px; border: 1px solid #eee; background: #fff;\"></div>");
    html.append("<script type=\"text/javascript\">\n");
    // Ensure the function exists before calling
    html.append("  if(typeof renderSymjaWebGL === 'function') {\n");
    html.append("     renderSymjaWebGL('" + containerId + "', " + jsonData + ");\n");
    html.append(
        "  } else { console.error('renderSymjaWebGL not found. Ensure symja_webgl.js is loaded.'); }\n");
    html.append("</script>");
    return html.toString();
  }

  private static String getHTMLTemplate(String jsonData) {
    StringBuilder js = new StringBuilder();
    js.append(
        "<!DOCTYPE html><html><head><title>Symja 3D</title><style>body { margin: 0; overflow: hidden; background-color: #f0f0f0; }</style>\n");

    // Import Map for Local Three.js
    js.append(
        "<script type=\"importmap\">{ \"imports\": { \"three\": \"/media/js/three/build/three.module.js\", \"three/addons/\": \"/media/js/three/examples/jsm/\" } }</script>\n");

    js.append("</head><body>\n");

    // Module script to load Three and expose it globally
    js.append("<script type=\"module\">\n");
    js.append("  import * as THREE_MODULE from 'three';\n");
    js.append("  import { OrbitControls } from 'three/addons/controls/OrbitControls.js';\n");
    js.append("  const THREE = { ...THREE_MODULE };\n");
    js.append("  THREE.OrbitControls = OrbitControls;\n");
    js.append("  window.THREE = THREE;\n");
    // Dispatch event so legacy scripts know THREE is ready?
    // Or simpler: We load symja_webgl.js as a standard script defer/async or dynamically
    js.append("</script>\n");

    // Load the common renderer
    js.append("<script type=\"text/javascript\" src=\"/media/js/symja_webgl.js\"></script>\n");

    js.append("<div id=\"webgl-container\" style=\"width: 100vw; height: 100vh;\"></div>\n");

    js.append("<script type=\"text/javascript\">\n");
    js.append("  window.addEventListener('load', function() {\n");
    // Small timeout or check to ensure THREE is globally available if module loading is slow
    js.append("     var checkTHREE = setInterval(function() {\n");
    js.append("         if (window.THREE && typeof renderSymjaWebGL === 'function') {\n");
    js.append("             clearInterval(checkTHREE);\n");
    js.append("             var data = " + jsonData + ";\n");
    js.append("             renderSymjaWebGL('webgl-container', data);\n");
    js.append("         }\n");
    js.append("     }, 100);\n");
    js.append("  });\n");
    js.append("</script>\n");

    js.append("</body></html>");
    return js.toString();
  }
}