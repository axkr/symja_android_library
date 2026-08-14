package org.matheclipse.core.graphics;

import java.awt.Color;
import java.util.List;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.svg.TickGenerator;
import org.matheclipse.core.graphics.webgl.Bounds3D;
import org.matheclipse.core.graphics.webgl.GraphicsOptions3D;
import org.matheclipse.core.graphics.webgl.Lighting3D;
import org.matheclipse.core.graphics.webgl.PrimitiveCollector3D;
import org.matheclipse.core.graphics.webgl.Style3D;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Converts a {@code Graphics3D} expression into the JSON scene description that
 * {@code symja_webgl.js} renders with Three.js.
 *
 * <p>
 * Option defaults, directive scoping, tick placement, the extent of the data — and the renderer
 * only turns the result into geometry. That keeps tick labels identical to the ones the 2D SVG path
 * produces, since both go through {@link TickGenerator}.
 */
public class WebGLGraphics3D {

  private static final ObjectMapper mapper = new ObjectMapper();

  /** Ticks past this many on one axis are dropped rather than drawn on top of each other. */
  private static final int MAX_TICKS = 20;

  /** The renderer source, read once from the classpath. */
  private static String rendererScript = null;

  /**
   * The contents of {@code symja_webgl.js}.
   *
   * <p>
   * A page that is served by the web front end links to the script, but a page written to a file
   * has no server to fetch it from and has to carry it inline. Both read it from here, so there is
   * only ever one copy of the renderer to keep in step with this converter.
   */
  public static synchronized String rendererScript() {
    if (rendererScript == null) {
      try (java.io.InputStream in = WebGLGraphics3D.class.getClassLoader()
          .getResourceAsStream("public/media/js/symja_webgl.js")) {
        rendererScript = in == null ? ""
            : new String(in.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
      } catch (java.io.IOException e) {
        rendererScript = "";
      }
    }
    return rendererScript;
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

  /** The scene JSON on its own, which the tests assert against. */
  public static String generateJSON(IAST graphics) {
    try {
      return mapper.writeValueAsString(buildScene(graphics));
    } catch (JsonProcessingException e) {
      return "{}";
    }
  }

  private static String generateOutput(IAST graphics, boolean isSnippet) {
    ObjectNode scene;
    try {
      scene = buildScene(graphics);
    } catch (RuntimeException e) {
      return isSnippet ? "<div>Error generating WebGL graphics</div>" : "";
    }
    double width = 360;
    double height = 360;
    if (scene.has("imageSize")) {
      ArrayNode size = (ArrayNode) scene.get("imageSize");
      width = size.get(0).asDouble(360);
      height = size.get(1).asDouble(360);
    }
    String json;
    try {
      json = mapper.writeValueAsString(scene);
    } catch (JsonProcessingException e) {
      return isSnippet ? "<div>Error generating WebGL graphics</div>" : "";
    }
    return isSnippet ? createSnippetHTML(json, width, height) : createPageHTML(json);
  }

  // ------------------------------------------------------------------- scene

  /**
   * The scene a {@code Graphics3D} describes: its elements in data coordinates, the range they are
   * drawn in, the ticks, the lights and the camera.
   *
   * <p>
   * A second renderer can take the result and only have to turn it into pictures.
   * {@link SVGGraphics3D} does exactly that, which is what keeps the interactive and the static
   * output showing the same graphic.
   */
  /**
   * Whether this is something {@link #buildScene} can draw.
   *
   * <p>
   * A plot given {@code PlotLegends} comes back as {@code Legended[Graphics3D[...], legend]}, and
   * the shape the scene builder reads. Anything that decides whether to render has to look through
   * that wrapper, or a plot renders until someone asks it for a legend and then silently prints as
   * text instead.
   */
  public static boolean isRenderable(IExpr expr) {
    if (expr.isAST(S.Legended, 3)) {
      expr = ((IAST) expr).arg1();
    }
    return expr.isASTSizeGE(S.Graphics3D, 2);
  }

  public static ObjectNode buildScene(IAST graphics) {
    ObjectNode root = mapper.createObjectNode();

    IAST target = graphics;
    String legendText = null;
    boolean showLegend = false;
    if (target.isAST(S.Legended)) {
      showLegend = true;
      if (target.argSize() >= 2 && !target.arg2().isAutomatic()) {
        legendText = GraphicsOptions3D.text(target.arg2());
      }
      IExpr content = target.arg1();
      if (content.isAST()) {
        target = (IAST) content;
      }
    }

    GraphicsOptions3D options = new GraphicsOptions3D();
    options.parse(target);

    ArrayNode elements = root.putArray("elements");
    PrimitiveCollector3D collector = new PrimitiveCollector3D(elements, options.scaling);
    if (target.argSize() >= 1) {
      collector.collect(target.arg1(), new Style3D());
    }

    double[][] ranges = resolveRanges(collector.bounds, options);
    writeScene(root, options, ranges, collector.bounds.diagonal());

    if (showLegend) {
      root.put("showLegend", true);
      if (legendText != null) {
        root.put("legendText", legendText);
      }
    }
    return root;
  }

  /**
   * The visible range per axis: an explicit {@code PlotRange} where one was given, and the extent
   * of the data everywhere else.
   */
  private static double[][] resolveRanges(Bounds3D bounds, GraphicsOptions3D options) {
    double[][] ranges = bounds.ranges();
    for (int i = 0; i < 3; i++) {
      if (options.plotRange[i] != null) {
        ranges[i] = options.plotRange[i].clone();
      }
    }
    return ranges;
  }

  private static void writeScene(ObjectNode root, GraphicsOptions3D options, double[][] ranges,
      double diagonal) {
    ArrayNode rangeNode = root.putArray("plotRange");
    for (double[] range : ranges) {
      rangeNode.addArray().add(range[0]).add(range[1]);
    }
    root.put("diagonal", diagonal);

    ArrayNode scaling = root.putArray("scaling");
    for (String s : options.scaling) {
      scaling.add(s);
    }

    ArrayNode axes = root.putArray("axes");
    for (boolean on : options.axes) {
      axes.add(on);
    }
    ArrayNode axesLabel = root.putArray("axesLabel");
    for (String label : options.axesLabel) {
      if (label == null) {
        axesLabel.addNull();
      } else {
        axesLabel.add(label);
      }
    }
    ArrayNode axesEdge = root.putArray("axesEdge");
    for (int i = 0; i < 3; i++) {
      if (options.axesEdgeNone[i]) {
        axesEdge.add("None");
      } else if (options.axesEdge[i] != null) {
        axesEdge.addArray().add(options.axesEdge[i][0]).add(options.axesEdge[i][1]);
      } else {
        axesEdge.add("Automatic");
      }
    }
    if (options.axesColor != null) {
      root.put("axesColor", rgb(options.axesColor));
    }
    root.put("axesThickness", options.axesThickness);

    root.put("boxed", options.boxed);
    if (options.boxColor != null) {
      root.put("boxColor", rgb(options.boxColor));
    }
    root.put("boxThickness", options.boxThickness);

    if (options.boxRatios != null) {
      root.putArray("boxRatios").add(options.boxRatios[0]).add(options.boxRatios[1])
          .add(options.boxRatios[2]);
    }
    if (options.background != null) {
      root.put("background", rgb(options.background));
      root.put("backgroundOpacity", options.background.getAlpha() / 255.0);
    }
    if (options.plotLabel != null) {
      root.put("plotLabel", options.plotLabel);
    }
    if (options.faceGrids) {
      root.put("faceGrids", true);
      if (options.faceGridsColor != null) {
        root.put("faceGridsColor", rgb(options.faceGridsColor));
      }
    }

    root.putArray("imageSize").add(options.imageSize[0]).add(options.imageSize[1]);
    root.putArray("viewPoint").add(options.viewPoint[0]).add(options.viewPoint[1])
        .add(options.viewPoint[2]);
    root.putArray("viewVertical").add(options.viewVertical[0]).add(options.viewVertical[1])
        .add(options.viewVertical[2]);
    if (options.viewCenter != null) {
      root.putArray("viewCenter").add(options.viewCenter[0]).add(options.viewCenter[1])
          .add(options.viewCenter[2]);
    }
    if (!Double.isNaN(options.viewAngle)) {
      root.put("viewAngle", options.viewAngle);
    }
    root.put("viewProjection", options.orthographic ? "Orthographic" : "Perspective");
    root.put("sphericalRegion", options.sphericalRegion);

    root.put("labelFontSize", options.labelFontSize);
    root.put("labelFontFamily", options.labelFontFamily);
    if (options.labelColor != null) {
      root.put("labelColor", rgb(options.labelColor));
    }
    if (options.ticksColor != null) {
      root.put("ticksColor", rgb(options.ticksColor));
    }

    writeTicks(root, options, ranges);

    List<Lighting3D.Light> lights = Lighting3D.parse(options.lighting);
    Lighting3D.write(root.putArray("lights"), lights);
  }

  /**
   * Tick positions and labels per axis.
   *
   * <p>
   * A logarithmic axis carries coordinates that have already been mapped through {@code log10}, so
   * the ticks are generated over the original values and their positions are mapped the same way.
   * That is what makes a log axis read 1, 10, 100 rather than 0, 1, 2.
   */
  private static void writeTicks(ObjectNode root, GraphicsOptions3D options, double[][] ranges) {
    ArrayNode ticksNode = root.putArray("ticks");
    for (int axis = 0; axis < 3; axis++) {
      ArrayNode axisTicks = ticksNode.addArray();
      if (!options.showTicks) {
        continue;
      }
      boolean log = "Log".equals(options.scaling[axis]);
      boolean reverse = "Reverse".equals(options.scaling[axis]);
      double lo = ranges[axis][0];
      double hi = ranges[axis][1];

      List<TickGenerator.Tick> ticks;
      if (options.ticksSpec[axis] != null) {
        ticks = TickGenerator.explicit(options.ticksSpec[axis]);
      } else if (log) {
        ticks = TickGenerator.logarithmic(Math.pow(10, lo), Math.pow(10, hi));
      } else if (reverse) {
        ticks = TickGenerator.linear(-hi, -lo);
      } else {
        ticks = TickGenerator.linear(lo, hi);
      }
      if (ticks == null || ticks.size() > MAX_TICKS) {
        continue;
      }
      for (TickGenerator.Tick tick : ticks) {
        double position = tick.value;
        if (log) {
          position = tick.value > 0 ? Math.log10(tick.value) : lo;
        } else if (reverse) {
          position = -tick.value;
        }
        if (position < lo - 1e-9 || position > hi + 1e-9) {
          continue;
        }
        ObjectNode node = axisTicks.addObject();
        node.put("position", position);
        node.put("label", tick.label);
      }
    }
  }

  private static int rgb(Color c) {
    return c.getRGB() & 0x00FFFFFF;
  }

  // -------------------------------------------------------------------- HTML

  private static String createSnippetHTML(String jsonData, double width, double height) {
    String containerId =
        "webgl_" + System.currentTimeMillis() + "_" + (int) (Math.random() * 10000);
    StringBuilder html = new StringBuilder();
    html.append("<div data-type=\"webgl\" id=\"").append(containerId).append("\" style=\"width: ")
        .append((int) Math.round(width)).append("px; height: ").append((int) Math.round(height))
        // the canvas is built from the width this ends up with, and a resize observer keeps it
        // in step, so letting the box narrow with the output column costs nothing
        .append("px; max-width: 100%; border: 1px solid #eee; background: #fff;\"></div>");
    html.append("<script type=\"text/javascript\">\n");
    html.append("  if (typeof renderSymjaWebGL === 'function') {\n");
    html.append("    renderSymjaWebGL('").append(containerId).append("', ").append(jsonData)
        .append(");\n");
    html.append("  } else {\n");
    // the page may still be loading three.js as a module, so queue for the loader to drain
    html.append("    window.SymjaWebGLQueue = window.SymjaWebGLQueue || [];\n");
    html.append("    window.SymjaWebGLQueue.push(['").append(containerId).append("', ")
        .append(jsonData).append("]);\n");
    html.append("  }\n");
    html.append("</script>");
    return html.toString();
  }

  private static String createPageHTML(String jsonData) {
    StringBuilder html = new StringBuilder();
    html.append("<!DOCTYPE html><html><head><meta charset=\"utf-8\"><title>Symja 3D</title>\n");
    html.append("<style>body { margin: 0; background-color: #ffffff; }</style>\n");
    html.append(
        "<script type=\"importmap\">{ \"imports\": { \"three\": \"/media/js/three/three.module.js\", \"three/addons/\": \"/media/js/three/jsm/\" } }</script>\n");
    html.append("</head><body>\n");
    html.append("<div id=\"webgl-container\" style=\"width: 100vw; height: 100vh;\"></div>\n");
    html.append("<script type=\"module\">\n");
    html.append("  import * as THREE_MODULE from 'three';\n");
    html.append("  import { OrbitControls } from 'three/addons/controls/OrbitControls.js';\n");
    html.append("  const THREE = { ...THREE_MODULE };\n");
    html.append("  THREE.OrbitControls = OrbitControls;\n");
    html.append("  window.THREE = THREE;\n");
    html.append("  window.SymjaWebGLQueue = window.SymjaWebGLQueue || [];\n");
    html.append("  window.SymjaWebGLQueue.push(['webgl-container', ").append(jsonData)
        .append("]);\n");
    html.append("</script>\n");
    html.append("<script type=\"text/javascript\" src=\"/media/js/symja_webgl.js\"></script>\n");
    html.append("</body></html>");
    return html.toString();
  }
}
