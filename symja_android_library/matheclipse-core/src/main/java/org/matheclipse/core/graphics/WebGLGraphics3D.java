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

  private static class GraphicsState implements Cloneable {
    Color color = new Color(200, 200, 255);
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

    public ComplexContext(IAST points, IAST vertexColors) {
      this.points = points;
      this.vertexColors = vertexColors;
    }
  }

  /**
   * Generates an HTML snippet (DIV + SCRIPT) to render the graphics embedded in a page. Assumes
   * THREE and THREE.OrbitControls are loaded globally. * @param graphics the graphics AST
   * 
   * @return HTML code string starting with &lt;div data-type="webgl"...
   */
  public static String generateHTMLSnippet(IAST graphics) {
    try {
      ObjectNode rootNode = mapper.createObjectNode();
      ArrayNode elementsArray = rootNode.putArray("elements");

      // Reuse existing processing logic
      if (graphics.isAST(S.SurfaceGraphics)) {
        processSurfaceGraphics(elementsArray, graphics, new GraphicsState());
      } else if (graphics.isAST(S.Graphics3D) && graphics.argSize() >= 1) {
        processExpr(elementsArray, graphics.arg1(), new GraphicsState(), null);
      } else if (graphics.argSize() >= 1) {
        processExpr(elementsArray, graphics.arg1(), new GraphicsState(), null);
      }

      String jsonData = mapper.writeValueAsString(rootNode);

      // Create a unique ID for this plot instance
      String containerId =
          "webgl_" + System.currentTimeMillis() + "_" + (int) (Math.random() * 10000);

      StringBuilder html = new StringBuilder();

      // 1. The Container DIV
      html.append("<div data-type=\"webgl\" id=\"").append(containerId).append(
          "\" style=\"width: 500px; height: 400px; border: 1px solid #eee; background: #fff;\"></div>");

      // 2. The Script
      html.append("<script type=\"text/javascript\">\n");
      html.append("(function() {\n");
      html.append("  var container = document.getElementById('" + containerId + "');\n");
      html.append("  if (!container) return;\n");

      html.append("  var width = container.clientWidth || 500;\n");
      html.append("  var height = container.clientHeight || 400;\n");

      html.append("  var scene = new THREE.Scene();\n");
      html.append("  scene.background = new THREE.Color(0xffffff);\n");

      html.append("  var camera = new THREE.PerspectiveCamera(45, width / height, 0.1, 10000);\n");
      html.append("  camera.up.set(0, 0, 1);\n");
      html.append("  camera.position.set(10, -10, 10);\n");

      html.append("  var renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });\n");
      html.append("  renderer.setSize(width, height);\n");
      html.append("  renderer.shadowMap.enabled = true;\n");
      html.append("  container.appendChild(renderer.domElement);\n");

      // Use global THREE.OrbitControls
      html.append("  var controls = new THREE.OrbitControls(camera, renderer.domElement);\n");
      html.append("  controls.enableDamping = true;\n");

      html.append("  var hemiLight = new THREE.HemisphereLight(0xffffff, 0x444444, 1.0);\n");
      html.append("  hemiLight.position.set(0, 0, 50);\n");
      html.append("  scene.add(hemiLight);\n");

      html.append("  var dl = new THREE.DirectionalLight(0xffffff, 0.5);\n");
      html.append("  dl.position.set(10, -20, 20);\n");
      html.append("  dl.castShadow = true;\n");
      html.append("  scene.add(dl);\n");

      // Inject JSON Data
      html.append("  var data = ").append(jsonData).append(";\n");

      // Material Helper
      html.append("  function getMat(el, isLine) {\n");
      html.append("      var params = {\n");
      html.append("          color: el.color,\n");
      html.append("          transparent: el.opacity < 1.0,\n");
      html.append("          opacity: el.opacity,\n");
      html.append("          side: THREE.DoubleSide,\n");
      html.append("          depthWrite: el.opacity >= 1.0,\n");
      html.append("          vertexColors: (el.vertexColors && el.vertexColors.length > 0)\n");
      html.append("      };\n");
      html.append("      if (isLine) {\n");
      html.append(
          "          if (el.dashed) return new THREE.LineDashedMaterial(Object.assign(params, {dashSize: 0.5, gapSize: 0.3, scale: 1}));\n");
      html.append(
          "          return new THREE.LineBasicMaterial(Object.assign(params, {linewidth: 2}));\n");
      html.append("      }\n");
      html.append(
          "      var mat = new THREE.MeshPhongMaterial(Object.assign(params, {shininess: 20, specular: 0x111111, flatShading: false}));\n");
      html.append(
          "      if (el.type === 'Polygon') { mat.polygonOffset = true; mat.polygonOffsetFactor = 1; mat.polygonOffsetUnits = 1; }\n");
      html.append("      return mat;\n");
      html.append("  }\n");

      // Geometry Builder
      html.append("  var objectsGroup = new THREE.Group();\n");
      html.append("  if (data.elements) {\n");
      html.append("      data.elements.forEach(function(el) {\n");
      html.append("          var mesh;\n");

      // --- Polygon ---
      html.append("          if (el.type === 'Polygon') {\n");
      html.append("              var geom = new THREE.BufferGeometry();\n");
      html.append(
          "              geom.setAttribute('position', new THREE.Float32BufferAttribute(el.points, 3));\n");
      html.append("              if (el.vertexColors && el.vertexColors.length > 0) {\n");
      html.append(
          "                  geom.setAttribute('color', new THREE.Float32BufferAttribute(el.vertexColors, 3));\n");
      html.append("              }\n");
      html.append("              geom.computeVertexNormals();\n");
      html.append("              mesh = new THREE.Mesh(geom, getMat(el, false));\n");

      // --- Sphere ---
      html.append("          } else if (el.type === 'Sphere') {\n");
      html.append(
          "              mesh = new THREE.Mesh(new THREE.SphereGeometry(el.radius, 32, 32), getMat(el, false));\n");
      html.append("              mesh.position.set(el.center[0], el.center[1], el.center[2]);\n");

      // --- Cuboid ---
      html.append("          } else if (el.type === 'Cuboid') {\n");
      html.append("              var w = el.max[0] - el.min[0];\n");
      html.append("              var h = el.max[1] - el.min[1];\n");
      html.append("              var d = el.max[2] - el.min[2];\n");
      html.append(
          "              mesh = new THREE.Mesh(new THREE.BoxGeometry(w, h, d), getMat(el, false));\n");
      html.append(
          "              mesh.position.set(el.min[0] + w/2, el.min[1] + h/2, el.min[2] + d/2);\n");

      // --- Cylinder ---
      html.append("          } else if (el.type === 'Cylinder') {\n");
      html.append(
          "              var s = new THREE.Vector3(el.start[0], el.start[1], el.start[2]);\n");
      html.append("              var e = new THREE.Vector3(el.end[0], el.end[1], el.end[2]);\n");
      html.append("              var h = s.distanceTo(e);\n");
      html.append(
          "              var geom = new THREE.CylinderGeometry(el.radius, el.radius, h, 32);\n");
      html.append("              geom.translate(0, h/2, 0);\n");
      html.append("              mesh = new THREE.Mesh(geom, getMat(el, false));\n");
      html.append("              mesh.position.copy(s);\n");
      html.append(
          "              mesh.quaternion.setFromUnitVectors(new THREE.Vector3(0, 1, 0), e.clone().sub(s).normalize());\n");

      // --- Cone ---
      html.append("          } else if (el.type === 'Cone') {\n");
      html.append(
          "              var s = new THREE.Vector3(el.start[0], el.start[1], el.start[2]);\n");
      html.append("              var e = new THREE.Vector3(el.end[0], el.end[1], el.end[2]);\n");
      html.append("              var h = s.distanceTo(e);\n");
      html.append("              var geom = new THREE.ConeGeometry(el.radius, h, 32);\n");
      html.append("              geom.translate(0, h/2, 0);\n");
      html.append("              mesh = new THREE.Mesh(geom, getMat(el, false));\n");
      html.append("              mesh.position.copy(s);\n");
      html.append(
          "              mesh.quaternion.setFromUnitVectors(new THREE.Vector3(0, 1, 0), e.clone().sub(s).normalize());\n");

      // --- Line ---
      html.append("          } else if (el.type === 'Line') {\n");
      html.append("             var pts = [];\n");
      html.append(
          "             for(var i=0; i<el.points.length; i+=3) pts.push(new THREE.Vector3(el.points[i], el.points[i+1], el.points[i+2]));\n");
      html.append("             var geom = new THREE.BufferGeometry().setFromPoints(pts);\n");
      html.append("             mesh = new THREE.Line(geom, getMat(el, true));\n");
      html.append("             if (el.dashed) mesh.computeLineDistances();\n");

      // --- Point ---
      html.append("          } else if (el.type === 'Point') {\n");
      html.append("             for(var i=0; i<el.points.length; i+=3) {\n");
      html.append(
          "                 var m = new THREE.Mesh(new THREE.SphereGeometry(0.08, 8, 8), getMat(el, false));\n");
      html.append(
          "                 m.position.set(el.points[i], el.points[i+1], el.points[i+2]);\n");
      html.append("                 objectsGroup.add(m);\n");
      html.append("             }\n");
      html.append("             mesh = null;\n");
      html.append("          }\n");

      html.append("          if (mesh) {\n");
      html.append("              if (el.opacity >= 1.0) mesh.castShadow = true;\n");
      html.append("              mesh.receiveShadow = true;\n");
      html.append("              objectsGroup.add(mesh);\n");
      html.append("          }\n");
      html.append("      });\n");
      html.append("  }\n");

      html.append("  scene.add(objectsGroup);\n");

      // Auto-Fit Camera
      html.append("  var box = new THREE.Box3().setFromObject(objectsGroup);\n");
      html.append("  if (!box.isEmpty()) {\n");
      html.append("      var center = box.getCenter(new THREE.Vector3());\n");
      html.append("      var size = box.getSize(new THREE.Vector3());\n");
      html.append("      var maxDim = Math.max(size.x, Math.max(size.y, size.z));\n");
      html.append("      var fov = camera.fov * (Math.PI / 180);\n");
      html.append("      var cameraZ = Math.abs(maxDim / 2 * Math.tan(fov * 2));\n");
      html.append(
          "      camera.position.set(center.x + maxDim, center.y - maxDim, center.z + maxDim);\n");
      html.append("      camera.lookAt(center);\n");
      html.append("      controls.target.copy(center);\n");
      html.append("  }\n");

      html.append(
          "  function animate() { requestAnimationFrame(animate); controls.update(); renderer.render(scene, camera); }\n");
      html.append("  animate();\n");
      html.append("})();\n");
      html.append("</script>");

      return html.toString();
    } catch (IOException e) {
      e.printStackTrace();
      return "Error generating WebGL";
    }
  }

  public static String generateHTML(IAST graphics) {
    try {
      ObjectNode rootNode = mapper.createObjectNode();
      ArrayNode elementsArray = rootNode.putArray("elements");

      // Check for Legended Wrapper and unwrap
      boolean showLegend = false;
      if (graphics.isAST(S.Legended)) {
        showLegend = true;
        IExpr content = graphics.arg1();
        if (content.isAST()) {
          graphics = (IAST) content;
        }
      }

      if (graphics.isAST(S.SurfaceGraphics)) {
        processSurfaceGraphics(elementsArray, graphics, new GraphicsState());
      } else if (graphics.isAST(S.Graphics3D) && graphics.argSize() >= 1) {
        processExpr(elementsArray, graphics.arg1(), new GraphicsState(), null);
      } else if (graphics.argSize() >= 1) {
        processExpr(elementsArray, graphics.arg1(), new GraphicsState(), null);
      }

      return getHTMLTemplate(mapper.writeValueAsString(rootNode), showLegend);
    } catch (IOException e) {
      e.printStackTrace();
      return "";
    }
  }

  private static void processExpr(ArrayNode array, IExpr expr, GraphicsState state,
      ComplexContext context) {
    if (expr.isList()) {
      GraphicsState scopedState = state.clone();
      IAST list = (IAST) expr;
      for (int i = 1; i < list.size(); i++) {
        processItem(array, list.get(i), scopedState, context);
      }
    } else {
      processItem(array, expr, state, context);
    }
  }

  private static void processItem(ArrayNode array, IExpr expr, GraphicsState state,
      ComplexContext context) {
    if (expr.isBuiltInSymbol()) {
      processSymbol((IBuiltInSymbol) expr, state);
      return;
    }

    if (!expr.isAST()) {
      return;
    }

    IAST ast = (IAST) expr;
    IExpr headExpr = ast.head();

    if (headExpr.isBuiltInSymbol()) {
      IBuiltInSymbol head = (IBuiltInSymbol) headExpr;
      int id = head.ordinal();

      switch (id) {
        case ID.List:
        case ID.GraphicsGroup:
          processExpr(array, ast, state, context);
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
        case ID.EdgeForm:
          break;
        case ID.SurfaceGraphics:
          processSurfaceGraphics(array, ast, state);
          break;
        case ID.GraphicsComplex:
          if (ast.argSize() >= 2) {
            IExpr ptsExpr = ast.arg1();
            IExpr primitives = ast.arg2();
            IAST pts = ptsExpr.isList() ? (IAST) ptsExpr : null;
            IAST vColors = extractOptionList(ast, S.VertexColors);
            ComplexContext newContext = new ComplexContext(pts, vColors);
            processExpr(array, primitives, state, newContext);
          }
          break;
        case ID.Line:
          createPolyNode(array, "Line", ast.arg1(), state, context,
              extractOptionList(ast, S.VertexColors));
          break;
        case ID.Polygon:
          createPolyNode(array, "Polygon", ast.arg1(), state, context,
              extractOptionList(ast, S.VertexColors));
          break;
        case ID.Point:
          createPolyNode(array, "Point", ast.arg1(), state, context,
              extractOptionList(ast, S.VertexColors));
          break;
        case ID.Sphere: {
          ObjectNode node = createBaseNode(array, "Sphere", state);
          node.set("center", vecToJson(ast.arg1()));
          node.put("radius", (ast.argSize() >= 2) ? getDouble(ast.arg2(), 1.0) : 1.0);
        }
          break;
        case ID.Cuboid: {
          ObjectNode node = createBaseNode(array, "Cuboid", state);
          node.set("min", vecToJson(ast.arg1()));
          node.set("max", (ast.argSize() >= 2) ? vecToJson(ast.arg2())
              : vecToJson(F.List(F.num(1), F.num(1), F.num(1))));
        }
          break;
        case ID.Cylinder: {
          ObjectNode node = createBaseNode(array, "Cylinder", state);
          IExpr coords = ast.arg1();
          double radius = (ast.argSize() >= 2) ? getDouble(ast.arg2(), 1.0) : 1.0;
          node.put("radius", radius);
          if (coords.isList() && ((IAST) coords).size() > 2) {
            node.set("start", vecToJson(((IAST) coords).get(1)));
            node.set("end", vecToJson(((IAST) coords).get(2)));
          } else {
            node.set("start", vecToJson(F.List(F.num(0), F.num(0), F.num(-1))));
            node.set("end", vecToJson(F.List(F.num(0), F.num(0), F.num(1))));
          }
        }
          break;
      }
    }
  }

  private static IAST extractOptionList(IAST ast, ISymbol optionName) {
    for (int i = 2; i < ast.size(); i++) {
      if (ast.get(i).isRuleAST()) {
        IAST rule = (IAST) ast.get(i);
        if (rule.arg1().equals(optionName) && rule.arg2().isList()) {
          return (IAST) rule.arg2();
        }
      }
    }
    return null;
  }

  private static void createPolyNode(ArrayNode array, String type, IExpr data, GraphicsState state,
      ComplexContext context, IAST localVertexColors) {
    ObjectNode node = createBaseNode(array, type, state);
    ArrayNode pointsJson = node.putArray("points");

    List<IExpr> allVertices = new ArrayList<>();
    List<Color> allColors = new ArrayList<>();

    if (data.isList()) {
      IAST listData = (IAST) data;
      if (listData.size() > 1) {
        IExpr first = listData.get(1);
        boolean isMulti = false;

        // Detect Multi-Polygon
        if (context != null) {
          // Indices mode: Polygon[{1,2,3}] vs Polygon[{{1,2,3}, {2,3,4}}]
          if (first.isList())
            isMulti = true;
        } else {
          // Coordinate mode: Polygon[{{x,y,z}, ...}] (Single) vs Polygon[{{{x,y,z}...}, ...}]
          // (Multi)
          if (first.isList()) {
            IAST firstList = (IAST) first;
            if (firstList.size() > 1 && firstList.get(1).isList()) {
              isMulti = true;
            }
          }
        }

        if (isMulti) {
          for (int i = 1; i < listData.size(); i++) {
            processSingleFace(listData.get(i), type, context, localVertexColors, i - 1, allVertices,
                allColors);
          }
        } else {
          processSingleFace(data, type, context, localVertexColors, 0, allVertices, allColors);
        }
      }
    }

    // Serialize Flat Array
    for (IExpr v : allVertices) {
      if (v.isList()) {
        IAST vList = (IAST) v;
        if (vList.size() >= 4) {
          pointsJson.add(getDouble(vList.get(1)));
          pointsJson.add(getDouble(vList.get(2)));
          pointsJson.add(getDouble(vList.get(3)));
        }
      }
    }

    if (!allColors.isEmpty() && allColors.size() == allVertices.size()) {
      ArrayNode colorsJson = node.putArray("vertexColors");
      for (Color c : allColors) {
        colorsJson.add(c.getRed() / 255.0);
        colorsJson.add(c.getGreen() / 255.0);
        colorsJson.add(c.getBlue() / 255.0);
      }
    }
  }

  private static void processSingleFace(IExpr faceData, String type, ComplexContext context,
      IAST localVertexColors, int faceIndex, List<IExpr> outVertices, List<Color> outColors) {

    List<IExpr> faceVerts = new ArrayList<>();
    List<Color> faceCols = new ArrayList<>();

    resolveComplexAttributes(faceData, context, localVertexColors, 0, faceVerts, faceCols);

    if (faceVerts.isEmpty())
      return;

    if (type.equals("Line") || type.equals("Point")) {
      outVertices.addAll(faceVerts);
      outColors.addAll(faceCols);
      return;
    }

    // Triangulate
    if (faceVerts.size() >= 3) {
      IExpr v0 = faceVerts.get(0);
      Color c0 = !faceCols.isEmpty() ? faceCols.get(0) : null;

      for (int i = 1; i < faceVerts.size() - 1; i++) {
        outVertices.add(v0);
        outVertices.add(faceVerts.get(i));
        outVertices.add(faceVerts.get(i + 1));

        if (c0 != null) {
          outColors.add(c0);
          outColors.add(faceCols.get(i));
          outColors.add(faceCols.get(i + 1));
        }
      }
    }
  }

  private static int resolveComplexAttributes(IExpr data, ComplexContext context,
      IAST localColorList, int localIndex, List<IExpr> outPoints, List<Color> outColors) {
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

        if (localColorList != null) {
          outColors.add(Color.WHITE);
        } else if (context.vertexColors != null && idx > 0 && idx < context.vertexColors.size()) {
          outColors.add(parseRawColor(context.vertexColors.get(idx)));
        } else if (context.vertexColors != null) {
          outColors.add(Color.WHITE);
        }

      } else {
        outPoints.add(el);
      }
      localIndex++;
    }
    return localIndex;
  }

  private static ArrayNode vecToJson(IExpr vec) {
    ArrayNode node = mapper.createArrayNode();
    if (vec.isList()) {
      IAST list = (IAST) vec;
      for (int i = 1; i < list.size(); i++) {
        node.add(getDouble(list.get(i)));
      }
    } else {
      node.add(0.0).add(0.0).add(0.0);
    }
    return node;
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

  private static void processSurfaceGraphics(ArrayNode array, IAST ast, GraphicsState state) {
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
          zValues.add(getDouble(rowList.get(j)));
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
    node.put("xMin", xMin);
    node.put("xMax", xMax);
    node.put("yMin", yMin);
    node.put("yMax", yMax);
    node.put("showMesh", mesh);
  }


  private static String getHTMLTemplate(String jsonData, boolean showLegend) {
    String legendHtml = "";
    if (showLegend) {
      // Simple Color Bar for Complex Phase
      legendHtml =
          "<div style='position:absolute; right:20px; top:50%; transform:translateY(-50%); text-align:center; font-family:sans-serif; font-size:12px; z-index:100; pointer-events:none;'>"
              + "<div style='margin-bottom:5px;'>&pi;</div>"
              + "<div style='width:20px; height:200px; background: linear-gradient(to top, red, yellow, green, cyan, blue, magenta, red); border:1px solid #888;'></div>"
              + "<div style='margin-top:5px;'>-&pi;</div>"
              + "<div style='position:absolute; right:25px; top:50%; transform:translateY(-50%); width:30px; border-top:1px solid #000;'></div>"
              + "<div style='position:absolute; right:30px; top:50%; transform:translateY(-50%); background:rgba(255,255,255,0.8); padding:2px;'>0</div>"
              + "</div>";
    }

    return "       <!DOCTYPE html>\r\n" + "        <html>\r\n" + "        <head>\r\n"
        + "            <title>Symja 3D</title>\r\n"
        + "            <style>body { margin: 0; overflow: hidden; background-color: #f0f0f0; }</style>\r\n"
        + "            <script type=\"importmap\">\r\n"
        + "              { \"imports\": { \"three\": \"https://unpkg.com/three@0.160.0/build/three.module.js\", \"three/addons/\": \"https://unpkg.com/three@0.160.0/examples/jsm/\" } }\r\n"
        + "            </script>\r\n" + "        </head>\r\n" + "        <body>\r\n" + legendHtml
        + "\r\n" + "            <script type=\"module\">\r\n"
        + "                import * as THREE from 'three';\r\n"
        + "                import { OrbitControls } from 'three/addons/controls/OrbitControls.js';\r\n"
        + "\r\n" + "                const scene = new THREE.Scene();\r\n"
        + "                scene.background = new THREE.Color(0xffffff);\r\n" + "\r\n"
        + "                const camera = new THREE.PerspectiveCamera(45, window.innerWidth/window.innerHeight, 0.1, 10000);\r\n"
        + "                camera.up.set(0, 0, 1);\r\n"
        + "                camera.position.set(10, -10, 10);\r\n" + "\r\n"
        + "                const renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });\r\n"
        + "                renderer.setSize(window.innerWidth, window.innerHeight);\r\n"
        + "                renderer.shadowMap.enabled = true;\r\n"
        + "                document.body.appendChild(renderer.domElement);\r\n" + "\r\n"
        + "                const controls = new OrbitControls(camera, renderer.domElement);\r\n"
        + "                controls.enableDamping = true;\r\n" + "\r\n"
        + "                const hemiLight = new THREE.HemisphereLight(0xffffff, 0x444444, 1.0);\r\n"
        + "                hemiLight.position.set(0, 0, 50);\r\n"
        + "                scene.add(hemiLight);\r\n" + "\r\n"
        + "                const dl = new THREE.DirectionalLight(0xffffff, 0.5);\r\n"
        + "                dl.position.set(10, -20, 20);\r\n"
        + "                dl.castShadow = true;\r\n" + "                scene.add(dl);\r\n"
        + "\r\n" + "                const data = " //
        + jsonData //
        + "\n" //
        + "                const objectsGroup = new THREE.Group();\r\n" + "\r\n"
        + "                function getMat(el, isLine = false) {\r\n"
        + "                    const params = {\r\n"
        + "                        color: el.color,\r\n"
        + "                        transparent: el.opacity < 1.0,\r\n"
        + "                        opacity: el.opacity,\r\n"
        + "                        side: THREE.DoubleSide,\r\n"
        + "                        depthWrite: el.opacity >= 1.0,\r\n"
        + "                        vertexColors: (el.vertexColors && el.vertexColors.length > 0)\r\n"
        + "                    };\r\n" + "                    if (isLine) {\r\n"
        + "                        if (el.dashed) {\r\n"
        + "                            return new THREE.LineDashedMaterial({ ...params, dashSize: 0.5, gapSize: 0.3, scale: 1 });\r\n"
        + "                        }\r\n"
        + "                        return new THREE.LineBasicMaterial({ ...params, linewidth: 2 });\r\n"
        + "                    }\r\n"
        + "                    const mat = new THREE.MeshPhongMaterial({ ...params, shininess: 20, specular: 0x111111, flatShading: false });\r\n"
        + "                    if (el.type === 'Polygon') {\r\n"
        + "                        mat.polygonOffset = true; mat.polygonOffsetFactor = 1; mat.polygonOffsetUnits = 1;\r\n"
        + "                    }\r\n" + "                    return mat;\r\n"
        + "                }\r\n" + "\r\n" + "                if (data.elements) {\r\n"
        + "                    data.elements.forEach(el => {\r\n"
        + "                        let mesh;\r\n"
        + "                        if (el.type === 'Polygon') {\r\n"
        + "                            const pts = el.points;\r\n"
        + "                            const vCols = el.vertexColors;\r\n"
        + "                            const geom = new THREE.BufferGeometry();\r\n"
        + "                            geom.setAttribute('position', new THREE.Float32BufferAttribute(pts, 3));\r\n"
        + "                            if (vCols) {\r\n"
        + "                                geom.setAttribute('color', new THREE.Float32BufferAttribute(vCols, 3));\r\n"
        + "                            }\r\n"
        + "                            geom.computeVertexNormals();\r\n"
        + "                            mesh = new THREE.Mesh(geom, getMat(el));\r\n"
        + "                        }\r\n"
        + "                        else if (el.type === 'Sphere') {\r\n"
        + "                            mesh = new THREE.Mesh(new THREE.SphereGeometry(el.radius, 32, 32), getMat(el));\r\n"
        + "                            mesh.position.set(el.center[0], el.center[1], el.center[2]);\r\n"
        + "                        }\r\n"
        + "                        else if (el.type === 'Cuboid') {\r\n"
        + "                             const w = el.max[0] - el.min[0], h = el.max[1] - el.min[1], d = el.max[2] - el.min[2];\r\n"
        + "                             mesh = new THREE.Mesh(new THREE.BoxGeometry(w, h, d), getMat(el));\r\n"
        + "                             mesh.position.set(el.min[0] + w/2, el.min[1] + h/2, el.min[2] + d/2);\r\n"
        + "                        }\r\n"
        + "                        else if (el.type === 'Cylinder') {\r\n"
        + "                             const s = new THREE.Vector3(...el.start), e = new THREE.Vector3(...el.end);\r\n"
        + "                             const h = s.distanceTo(e);\r\n"
        + "                             const geom = new THREE.CylinderGeometry(el.radius, el.radius, h, 32);\r\n"
        + "                             geom.translate(0, h/2, 0);\r\n"
        + "                             mesh = new THREE.Mesh(geom, getMat(el));\r\n"
        + "                             mesh.position.copy(s);\r\n"
        + "                             mesh.quaternion.setFromUnitVectors(new THREE.Vector3(0, 1, 0), e.clone().sub(s).normalize());\r\n"
        + "                        }\r\n"
        + "                        else if (el.type === 'Line') {\r\n"
        + "                            const pts = [];\r\n"
        + "                            for(let i=0; i<el.points.length; i+=3) pts.push(new THREE.Vector3(el.points[i], el.points[i+1], el.points[i+2]));\r\n"
        + "                            const geom = new THREE.BufferGeometry().setFromPoints(pts);\r\n"
        + "                            mesh = new THREE.Line(geom, getMat(el, true));\r\n"
        + "                            if (el.dashed) mesh.computeLineDistances();\r\n"
        + "                        }\r\n"
        + "                        else if (el.type === 'Point') {\r\n"
        + "                            for(let i=0; i<el.points.length; i+=3) {\r\n"
        + "                                const m = new THREE.Mesh(new THREE.SphereGeometry(0.08, 16, 16), getMat(el));\r\n"
        + "                                m.position.set(el.points[i], el.points[i+1], el.points[i+2]);\r\n"
        + "                                objectsGroup.add(m);\r\n"
        + "                            }\r\n" + "                            mesh = null;\r\n"
        + "                        }\r\n" + "\r\n" + "                        if (mesh) {\r\n"
        + "                            if (el.opacity >= 1.0) mesh.castShadow = true;\r\n"
        + "                            mesh.receiveShadow = true;\r\n"
        + "                            objectsGroup.add(mesh);\r\n"
        + "                        }\r\n" + "                    });\r\n" + "                }\r\n"
        + "\r\n" + "                scene.add(objectsGroup);\r\n" + "\r\n"
        + "                const box = new THREE.Box3().setFromObject(objectsGroup);\r\n"
        + "                if (!box.isEmpty()) {\r\n"
        + "                    const center = box.getCenter(new THREE.Vector3());\r\n"
        + "                    const size = box.getSize(new THREE.Vector3());\r\n"
        + "                    const maxDim = Math.max(size.x, Math.max(size.y, size.z));\r\n"
        + "                    const fov = camera.fov * (Math.PI / 180);\r\n"
        + "                    let cameraZ = Math.abs(maxDim / 2 * Math.tan(fov * 2));\r\n"
        + "                    camera.position.set(center.x + maxDim, center.y - maxDim, center.z + maxDim);\r\n"
        + "                    camera.lookAt(center);\r\n"
        + "                    controls.target.copy(center);\r\n"
        + "                    const helper = new THREE.Box3Helper(box, 0x888888);\r\n"
        + "                    scene.add(helper);\r\n" + "                }\r\n" + "\r\n"
        + "                function animate() { requestAnimationFrame(animate); controls.update(); renderer.render(scene, camera); }\r\n"
        + "                animate();\r\n" + "\r\n"
        + "                window.addEventListener('resize', () => {\r\n"
        + "                    camera.aspect = window.innerWidth / window.innerHeight;\r\n"
        + "                    camera.updateProjectionMatrix();\r\n"
        + "                    renderer.setSize(window.innerWidth, window.innerHeight);\r\n"
        + "                });\r\n" + "            </script>\r\n" + "        </body>\r\n"
        + "        </html>";
  }
}