package org.matheclipse.core.graphics;

import static j2html.TagCreator.rawHtml;
import static j2html.TagCreator.tag;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Collectors;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.RGBColor;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;

/**
 * Advanced 2D Graphics to SVG converter.
 */
public class SVGGraphics {

  private static class GraphicState implements Cloneable {
    Color strokeColor = Color.BLACK;
    Color fillColor = Color.BLACK; // Default fill is Black for Disk/Rect/Poly
    Color faceColor = null;
    Color edgeColor = null;
    boolean edgeFormSet = false;

    double strokeWidth = 1.0;
    double opacity = 1.0;
    double edgeOpacity = 1.0;
    double pointRadius = 3.0;

    String dashArray = "none";
    String lineCap = "butt";
    String lineJoin = "miter";
    double arrowHeadScale = 0.05;
    String fontFamily = "sans-serif";
    double fontSize = 12.0;

    List<double[]> complexVertices = null;

    @Override
    public GraphicState clone() {
      try {
        return (GraphicState) super.clone();
      } catch (CloneNotSupportedException e) {
        return new GraphicState();
      }
    }

    Color getEffectiveFillColor() {
      return (faceColor != null) ? faceColor : fillColor;
    }
  }

  private static class Options {
    IExpr axes = S.False;
    double[] axesOrigin = null;
    GraphicState axesStyle = new GraphicState();

    boolean frame = false;
    GraphicState frameStyle = new GraphicState();

    GraphicState globalStyle = new GraphicState();

    // Initialize default grid style (Light Gray, Thin)
    GraphicState gridLinesStyle = new GraphicState();
    {
      gridLinesStyle.strokeColor = new Color(200, 200, 200);
      gridLinesStyle.strokeWidth = 0.5;
      gridLinesStyle.opacity = 1.0;
    }

    Color background = null;
    double aspectRatio = Double.NaN;
    double[] imageSize = {360, 360};

    // [0][0]=xMin, [0][1]=xMax, [1][0]=yMin, [1][1]=yMax. Double.NaN indicates "Automatic"
    double[][] plotRange = null;
    double plotRangePadding = 0.05;
    boolean plotRangeAutomatic = true; // Default to heuristic

    // "None", "Log10", or "Log"
    String scalingX = "None";
    String scalingY = "None";

    IExpr prolog = null;
    IExpr epilog = null;

    IExpr gridLines = null;

    // Legend support
    IExpr plotLegends = null;
    IExpr plotStyleRaw = null; // To access colors by index
    IExpr frameTicks = S.None;
    boolean joined = false;
  }


  /**
   * Generates the root SVG Element (ContainerTag) for the given graphics expression. This allows
   * valid modification of the SVG tag before rendering (e.g. adding x, y attributes).
   * 
   * @param graphicsExpr
   * @return
   */
  public ContainerTag buildSVGTag(IAST graphicsExpr) {
    if (graphicsExpr.isList() || graphicsExpr.isAST(S.GraphicsRow)) {
      // GraphicsRow not yet supported in this refactoring for buildSVGTag, fall back roughly
      // But toSVGRow returns string. We might need a dummy wrapper if used here.
      // For Inset, usually single Graphics is used.
      return null;
    }

    resetBounds();
    parseOptions(graphicsExpr);
    adjustPadding();

    if (graphicsExpr.argSize() >= 1)
      scanBounds(graphicsExpr.arg1(), options.globalStyle.clone());
    refineDataBounds();
    adjustBoundsForLogScale();
    if (options.plotLegends != null) {
      if (options.plotLegends.isList() || isBarLegend(options.plotLegends))
        paddingRight += LEGEND_WIDTH;
    }
    calculateViewport();

    List<DomContent> elements = new ArrayList<>();
    String plotAreaId = "plotArea" + idSuffix;
    String gradientId = "sunsetGradient" + idSuffix;

    // 1. Base Canvas (White)
    elements.add(tag("rect").attr("width", "100%").attr("height", "100%").attr("fill", "white"));

    // 2. Plot Area
    double plotX1 = paddingLeft;
    double plotX2 = paddingLeft + (mapMaxX - mapMinX) * scaleX;
    double plotY2 = options.imageSize[1] - paddingBottom;
    double plotY1 = plotY2 - (mapMaxY - mapMinY) * scaleY;
    double clipW = Math.max(0, plotX2 - plotX1);
    double clipH = Math.max(0, plotY2 - plotY1);

    // 3. User Background
    if (options.background != null) {
      elements
          .add(tag("rect").attr("x", fmt(plotX1)).attr("y", fmt(plotY1)).attr("width", fmt(clipW))
              .attr("height", fmt(clipH)).attr("fill", colorToCss(options.background)));
    }

    if (options.prolog != null) {
      ContainerTag<?> prologGroup = tag("g").attr("id", "prolog");
      processElement(options.prolog, options.globalStyle.clone(), prologGroup);
      elements.add(prologGroup);
    }

    if (options.gridLines != null && !options.gridLines.isFalse() && !options.gridLines.isNone()) {
      ContainerTag<?> gridGroup = tag("g").attr("class", "grid");
      drawGridLines(gridGroup);
      elements.add(gridGroup);
    }

    ContainerTag<?> defs = tag("defs");
    boolean hasDefs = false;

    if (options.plotLegends != null && isBarLegend(options.plotLegends)) {
      ContainerTag<?> gradient = tag("linearGradient").attr("id", gradientId).attr("x1", "0%")
          .attr("y1", "100%").attr("x2", "0%").attr("y2", "0%");
      double[][] colors = GraphicsOptions.SUNSET_COLORS;
      int n = colors.length - 1;
      for (int i = 0; i < colors.length; i++) {
        double offset = (double) i / n * 100.0;
        String color = String.format(Locale.US, "rgb(%d,%d,%d)", (int) (colors[i][0] * 255),
            (int) (colors[i][1] * 255), (int) (colors[i][2] * 255));
        gradient.with(tag("stop").attr("offset", String.format(Locale.US, "%.1f%%", offset))
            .attr("style", "stop-color:" + color + ";stop-opacity:1"));
      }
      defs.with(gradient);
      hasDefs = true;
    }

    defs.with(tag("clipPath").attr("id", plotAreaId).with(tag("rect").attr("x", fmt(plotX1))
        .attr("y", fmt(plotY1)).attr("width", fmt(clipW)).attr("height", fmt(clipH))));
    hasDefs = true;
    if (hasDefs)
      elements.add(defs);

    ContainerTag<?> mainGroup =
        tag("g").attr("id", "main").attr("clip-path", "url(#" + plotAreaId + ")");
    if (graphicsExpr.argSize() >= 1) {
      ContainerTag<?> contentGroup =
          tag("g").attr("font-family", "sans-serif").attr("font-size", "12.0");
      processElement(graphicsExpr.arg1(), options.globalStyle.clone(), contentGroup);
      mainGroup.with(contentGroup);
    }
    elements.add(mainGroup);

    ContainerTag<?> axesGroup = tag("g").attr("class", "axes");
    if (!options.axes.isFalse() && !options.axes.isNone())
      drawAxesWithTicks(axesGroup);
    if (options.frame)
      drawFrame(axesGroup);
    if (axesGroup.getNumChildren() > 0)
      elements.add(axesGroup);

    if (options.plotLegends != null) {
      ContainerTag<?> legendGroup = tag("g").attr("class", "legends");
      drawLegends(legendGroup, gradientId);
      elements.add(legendGroup);
    }

    if (options.epilog != null) {
      ContainerTag<?> epilogGroup = tag("g").attr("id", "epilog");
      processElement(options.epilog, options.globalStyle.clone(), epilogGroup);
      elements.add(epilogGroup);
    }
    return tag("svg")//
        .attr("xmlns", "http://www.w3.org/2000/svg")//
        .attr("width", fmt(options.imageSize[0])) //
        .attr("height", fmt(options.imageSize[1])) //
        .attr("style", "max-width: 100%; height: auto;") // <-- PREVENTS CLIPPING
        .attr("viewBox",
            String.format(Locale.US, "0 0 %.0f %.0f", options.imageSize[0], options.imageSize[1]))//
        .with(elements);
  }

  private void drawInset(IAST ast, GraphicState state, ContainerTag<?> parent) {
    IExpr obj = ast.arg1();
    if (obj.isAST() && (obj.isAST(S.Graphics) || obj.isAST(S.Graphics3D))) {
      // Handle nested graphics
      double[] pos = {0.0, 0.0};
      if (ast.argSize() >= 2) {
        pos = parsePoint(ast.arg2(), state);
      }

      IExpr oposExpr = (ast.argSize() >= 3) ? ast.arg3() : null;
      // Default opos is center {0,0} relative to object, but if object is Graphics, center usually
      // means geometric center
      // For Graphics, typical default alignment is Center.

      IExpr sizeExpr = (ast.argSize() >= 4) ? ast.arg4() : null;

      // Determine dimensions of the inset
      double w = 0, h = 0;
      boolean explicitSize = false;

      if (sizeExpr != null) {
        if (sizeExpr.isList()) {
          IAST szList = (IAST) sizeExpr;
          double valW = getDouble(szList.arg1(), Double.NaN);
          double valH = getDouble(szList.arg2(), Double.NaN);
          if (!Double.isNaN(valW) && !Double.isNaN(valH)) {
            // Size is in outer coordinates
            w = Math.abs(valW * scaleX);
            h = Math.abs(valH * scaleY);
            explicitSize = true;
          }
        } else if (sizeExpr.isNumber()) {
          double val = getDouble(sizeExpr);
          w = Math.abs(val * scaleX);
          // h calculated from aspect ratio later
          explicitSize = true;
        }
      }

      // If we don't have explicit size, we might use a default or try to determine.
      if (!explicitSize) {
        w = options.imageSize[0] / 3.0; // Default to 1/3 of view
        h = options.imageSize[1] / 3.0;
      } else if (h == 0) {
        // Maintain aspect ratio if possible (needs internal aspect ratio)
        h = w; // fallback
      }

      SVGGraphics subGraphics = new SVGGraphics(w, h);
      ContainerTag<?> subSVG = subGraphics.buildSVGTag((IAST) obj);
      if (subSVG == null)
        return; // Error or unsupported

      // If we only had width, try to correct height if aspect ratio is available from subGraphics
      // options
      if (explicitSize && sizeExpr.isNumber()) {
        // If aspect ratio is known
        double ar = subGraphics.options.aspectRatio;
        if (Double.isNaN(ar)) {
          // Try to imply from plot range
          double rangeX = subGraphics.mapMaxX - subGraphics.mapMinX;
          double rangeY = subGraphics.mapMaxY - subGraphics.mapMinY;
          if (rangeX > 0)
            ar = rangeY / rangeX;
          else
            ar = 1.0;
        }
        h = w * ar;
        // Update height attribute
        subSVG.attr("height", fmt(h));
        subSVG.attr("viewBox", String.format(Locale.US, "0 0 %.0f %.0f", w, h));
        // Also update subGraphics imageSize for correct alignment mapping?
        // The subGraphics was built with w,w. Recalculation is hard.
        // Ideally we should have known H before building.
        // However, let's assume square default for fallback or use what we got.
      }


      // Calculate position
      double outerX = mapX(pos[0]);
      double outerY = mapY(pos[1]);

      // Alignment (opos)
      double alignX = 0, alignY = 0; // Displacement in pixels from top-left of sub-image

      if (oposExpr != null) {
        if (oposExpr.isAST(S.ImageScaled) && oposExpr.first().isList()) {
          // ImageScaled[{x,y}] - 0..1 relative to image size
          IAST is = (IAST) oposExpr.first();
          double rx = getDouble(is.arg1(), 0.5);
          double ry = getDouble(is.arg2(), 0.5); // Mathematica 0,0 is bottom-left?
          // In ImageScaled, {0,0} is bottom-left usually.
          // In SVG, 0,0 is top-left.
          // Let's assume standard Mathematica coords for ImageScaled: 0,0 bottom-left, 1,1
          // top-right.

          alignX = rx * w;
          alignY = (1.0 - ry) * h;

        } else {
          // Coordinates in the object's system
          double[] op = parsePoint(oposExpr, state); // Parse using current state? No, raw parse.
          // We need to map op to subGraphics pixels.
          double opPx = subGraphics.mapX(op[0]);
          double opPy = subGraphics.mapY(op[1]);
          if (Double.isFinite(opPx) && Double.isFinite(opPy)) {
            alignX = opPx; // relative to subGraphics origin (0, top-left of image) which includes
                           // padding
            alignY = opPy;
            // But mapX/mapY includes padding.
            // subSVG is the whole image.
            // So mapX returns pixel coordinate relative to top-left of image.
            // Yes, that's exactly what we need.
          } else {
            // Fallback to center
            alignX = w / 2.0;
            alignY = h / 2.0;
          }
        }
      } else {
        // Default alignment: Center
        alignX = w / 2.0;
        alignY = h / 2.0;
      }

      double finalX = outerX - alignX;
      double finalY = outerY - alignY;

      subSVG.attr("x", fmt(finalX));
      subSVG.attr("y", fmt(finalY));

      // We must ensure width/height match what we calculated
      subSVG.attr("width", fmt(w));
      subSVG.attr("height", fmt(h));

      parent.with(subSVG);

    } else {
      drawText(ast, state, parent);
    }
  }


  // --- Fields ---

  private final Options options = new Options();
  private final String idSuffix;
  private boolean imageSizeSet = false;

  // Data Bounds (Raw Values)
  private double dataMinX = Double.MAX_VALUE, dataMaxX = -Double.MAX_VALUE;
  private double dataMinY = Double.MAX_VALUE, dataMaxY = -Double.MAX_VALUE;

  // Collection for heuristic analysis
  private final List<Double> allYValues = new ArrayList<>();

  // Mapped Bounds (Log/Linear transformed)
  private double mapMinX, mapMaxX, mapMinY, mapMaxY;
  private double scaleX, scaleY;

  // Margins
  private double paddingLeft = 50;
  private double paddingBottom = 30;
  private double paddingTop = 20;
  private double paddingRight = 20;

  private static final double LEGEND_WIDTH = 100.0;
  private static final double LOG_MIN_CLAMP = 1e-10;

  public SVGGraphics() {
    this.idSuffix = "_" + Integer.toHexString(System.identityHashCode(this));
    this.imageSizeSet = false;
  }

  public SVGGraphics(double w, double h) {
    this();
    options.imageSize = new double[] {w, h};
    this.imageSizeSet = true;
  }

  public double[] getImageSize() {
    return options.imageSize;
  }

  public String toSVG(IAST graphicsExpr) {
    return toSVG(graphicsExpr, true);
  }

  public String toSVG(IAST graphicsExpr, boolean withSVGTag) {
    if (graphicsExpr.isList() || graphicsExpr.isAST(S.GraphicsRow)) {
      return toSVGRow(graphicsExpr, withSVGTag);
    } else if (graphicsExpr.isAST(S.GraphicsColumn)) {
      return toSVGColumn(graphicsExpr, withSVGTag);
    } else if (graphicsExpr.isAST(S.GraphicsGrid)) {
      return toSVGGrid(graphicsExpr, withSVGTag);
    }

    try {
      resetBounds();
      parseOptions(graphicsExpr);
      adjustPadding();

      if (graphicsExpr.argSize() >= 1)
        scanBounds(graphicsExpr.arg1(), options.globalStyle.clone());
      refineDataBounds();
      adjustBoundsForLogScale();
      if (options.plotLegends != null) {
        if (options.plotLegends.isList() || isBarLegend(options.plotLegends))
          paddingRight += LEGEND_WIDTH;
      }
      calculateViewport();

      List<DomContent> elements = new ArrayList<>();
      String plotAreaId = "plotArea" + idSuffix;
      String gradientId = "sunsetGradient" + idSuffix;

      // 1. Base Canvas (White)
      elements.add(tag("rect").attr("width", "100%").attr("height", "100%").attr("fill", "white"));

      // 2. Plot Area
      double plotX1 = paddingLeft;
      double plotX2 = paddingLeft + (mapMaxX - mapMinX) * scaleX;
      double plotY2 = options.imageSize[1] - paddingBottom;
      double plotY1 = plotY2 - (mapMaxY - mapMinY) * scaleY;
      double clipW = Math.max(0, plotX2 - plotX1);
      double clipH = Math.max(0, plotY2 - plotY1);

      // 3. User Background
      if (options.background != null) {
        elements
            .add(tag("rect").attr("x", fmt(plotX1)).attr("y", fmt(plotY1)).attr("width", fmt(clipW))
                .attr("height", fmt(clipH)).attr("fill", colorToCss(options.background)));
      }

      if (options.prolog != null) {
        ContainerTag<?> prologGroup = tag("g").attr("id", "prolog");
        processElement(options.prolog, options.globalStyle.clone(), prologGroup);
        elements.add(prologGroup);
      }

      if (options.gridLines != null && !options.gridLines.isFalse()
          && !options.gridLines.isNone()) {
        ContainerTag<?> gridGroup = tag("g").attr("class", "grid");
        drawGridLines(gridGroup);
        elements.add(gridGroup);
      }

      ContainerTag<?> defs = tag("defs");
      boolean hasDefs = false;

      if (options.plotLegends != null && isBarLegend(options.plotLegends)) {
        ContainerTag<?> gradient = tag("linearGradient").attr("id", gradientId).attr("x1", "0%")
            .attr("y1", "100%").attr("x2", "0%").attr("y2", "0%");
        double[][] colors = GraphicsOptions.SUNSET_COLORS;
        int n = colors.length - 1;
        for (int i = 0; i < colors.length; i++) {
          double offset = (double) i / n * 100.0;
          String color = String.format(Locale.US, "rgb(%d,%d,%d)", (int) (colors[i][0] * 255),
              (int) (colors[i][1] * 255), (int) (colors[i][2] * 255));
          gradient.with(tag("stop").attr("offset", String.format(Locale.US, "%.1f%%", offset))
              .attr("style", "stop-color:" + color + ";stop-opacity:1"));
        }
        defs.with(gradient);
        hasDefs = true;
      }

      defs.with(tag("clipPath").attr("id", plotAreaId).with(tag("rect").attr("x", fmt(plotX1))
          .attr("y", fmt(plotY1)).attr("width", fmt(clipW)).attr("height", fmt(clipH))));
      hasDefs = true;
      if (hasDefs)
        elements.add(defs);

      ContainerTag<?> mainGroup =
          tag("g").attr("id", "main").attr("clip-path", "url(#" + plotAreaId + ")");
      if (graphicsExpr.argSize() >= 1) {
        ContainerTag<?> contentGroup =
            tag("g").attr("font-family", "sans-serif").attr("font-size", "12.0");
        processElement(graphicsExpr.arg1(), options.globalStyle.clone(), contentGroup);
        mainGroup.with(contentGroup);
      }
      elements.add(mainGroup);

      ContainerTag<?> axesGroup = tag("g").attr("class", "axes");
      if (!options.axes.isFalse() && !options.axes.isNone())
        drawAxesWithTicks(axesGroup);
      if (options.frame)
        drawFrame(axesGroup);
      if (axesGroup.getNumChildren() > 0)
        elements.add(axesGroup);

      if (options.plotLegends != null) {
        ContainerTag<?> legendGroup = tag("g").attr("class", "legends");
        drawLegends(legendGroup, gradientId);
        elements.add(legendGroup);
      }

      if (options.epilog != null) {
        ContainerTag<?> epilogGroup = tag("g").attr("id", "epilog");
        processElement(options.epilog, options.globalStyle.clone(), epilogGroup);
        elements.add(epilogGroup);
      }

      if (withSVGTag) {
        return tag("svg") //
            .attr("xmlns", "http://www.w3.org/2000/svg") //
            .attr("width", fmt(options.imageSize[0])) //
            .attr("height", fmt(options.imageSize[1])) //
            .attr("style", "max-width: 100%; height: auto;") // <-- PREVENTS CLIPPING
            .attr("viewBox",
                String.format(Locale.US, "0 0 %.0f %.0f", options.imageSize[0],
                    options.imageSize[1])) //
            .with(elements) //
            .render();
      } else {
        return elements.stream().map(DomContent::render).collect(Collectors.joining("\n"));
      }
    } catch (RuntimeException rex) {
      Errors.printMessage(S.Graphics, rex);
      if (Config.SHOW_STACKTRACE) {
        rex.printStackTrace();
      }
    }
    return null;
  }

  private void adjustBoundsForLogScale() {
    if (dataMinX == Double.MAX_VALUE) {
      dataMinX = isLog(options.scalingX) ? 0.1 : 0.0;
      dataMaxX = isLog(options.scalingX) ? 10.0 : 1.0;
    }
    if (dataMinY == Double.MAX_VALUE) {
      dataMinY = isLog(options.scalingY) ? 0.1 : 0.0;
      dataMaxY = isLog(options.scalingY) ? 10.0 : 1.0;
    }
    if (isLog(options.scalingX) && dataMinX <= 0)
      dataMinX = LOG_MIN_CLAMP;
    if (isLog(options.scalingY) && dataMinY <= 0)
      dataMinY = LOG_MIN_CLAMP;

    if (Math.abs(dataMinX - dataMaxX) < 1e-15) {
      if (isLog(options.scalingX)) {
        dataMinX /= 2.0;
        dataMaxX *= 2.0;
      } else {
        dataMinX -= 0.5;
        dataMaxX += 0.5;
      }
    }
    if (Math.abs(dataMinY - dataMaxY) < 1e-15) {
      if (isLog(options.scalingY)) {
        dataMinY /= 2.0;
        dataMaxY *= 2.0;
      } else {
        dataMinY -= 0.5;
        dataMaxY += 0.5;
      }
    }
  }

  private void adjustPadding() {
    boolean showX = false;
    boolean showY = false;
    if (options.axes.isTrue()) {
      showX = true;
      showY = true;
    } else if (options.axes.isList2()) {
      showX = ((IAST) options.axes).arg1().isTrue();
      showY = ((IAST) options.axes).arg2().isTrue();
    }

    // Shrink padding to a tight 5px margin if no axes/frames require the space
    paddingLeft = (showY || options.frame) ? 50 : 5;
    paddingBottom = (showX || options.frame) ? 30 : 5;
    paddingTop = options.frame ? 20 : 5;
    paddingRight = options.frame ? 20 : 5;
  }

  private void applyState(ContainerTag<?> t, GraphicState s, String strokeOverride,
      String fillOverride) {
    String stroke = (strokeOverride != null) ? strokeOverride : colorToCss(s.strokeColor);
    String fill = (fillOverride != null) ? fillOverride : colorToCss(s.getEffectiveFillColor());

    t.attr("fill", fill).attr("fill-opacity", fmt(s.opacity)).attr("stroke", stroke)
        .attr("stroke-width", fmt(s.strokeWidth)).attr("stroke-opacity", fmt(s.edgeOpacity))
        .attr("stroke-dasharray", s.dashArray).attr("stroke-linecap", s.lineCap)
        .attr("stroke-linejoin", s.lineJoin);
  }

  private void refineDataBounds() {
    if (!options.plotRangeAutomatic || allYValues.size() < 10)
      return;

    Collections.sort(allYValues);
    int n = allYValues.size();
    double p10 = allYValues.get(n / 10);
    double p90 = allYValues.get(9 * n / 10);
    double bodyRange = p90 - p10;

    if (bodyRange <= 1e-10) {
      double q1 = allYValues.get(n / 4);
      double q3 = allYValues.get(3 * n / 4);
      if (q3 - q1 > 1e-10) {
        p10 = q1;
        p90 = q3;
        bodyRange = q3 - q1;
      } else {
        return;
      }
    }

    double expansion = 1.0;
    double softMin = p10 - expansion * bodyRange;
    double softMax = p90 + expansion * bodyRange;

    double q1 = allYValues.get(n / 4);
    double q3 = allYValues.get(3 * n / 4);
    double iqr = q3 - q1;
    double lowerFence = q1 - 3.0 * iqr;
    double upperFence = q3 + 3.0 * iqr;

    double targetMin = Math.max(softMin, lowerFence);
    double targetMax = Math.min(softMax, upperFence);

    double newMin = dataMinY;
    double newMax = dataMaxY;

    for (int i = 0; i < n; i++) {
      if (allYValues.get(i) >= targetMin) {
        newMin = allYValues.get(i);
        break;
      }
    }
    for (int i = n - 1; i >= 0; i--) {
      if (allYValues.get(i) <= targetMax) {
        newMax = allYValues.get(i);
        break;
      }
    }

    if (newMin > dataMinY)
      dataMinY = newMin;
    if (newMax < dataMaxY)
      dataMaxY = newMax;
  }

  private String toSVGColumn(IAST graphicsExpr, boolean withSVGTag) {
    IAST items = graphicsExpr;
    // Extract the inner list and parse GraphicsColumn options (like ImageSize)
    if (graphicsExpr.isAST(S.GraphicsColumn)) {
      if (graphicsExpr.argSize() >= 1 && graphicsExpr.arg1().isList()) {
        items = (IAST) graphicsExpr.arg1();
      } else {
        return "";
      }
      parseOptions(graphicsExpr);
    }

    int n = items.argSize();
    if (n == 0)
      return "";

    double gap = 20.0;
    double totalW = options.imageSize[0];
    double totalH = options.imageSize[1];

    // Distribute total height equally among the items, minus the gaps
    double itemH = (totalH - gap * (n - 1)) / n;
    double itemW = totalW;

    List<String> innerSvgs = new ArrayList<>();
    List<Double> sizesW = new ArrayList<>();
    List<Double> sizesH = new ArrayList<>();

    // Process each child graphic using the scaled dimensions
    for (int i = 1; i <= n; i++) {
      IExpr expr = items.get(i);
      SVGGraphics sub = new SVGGraphics(itemW, itemH);
      String svg = sub.toSVG((IAST) expr, false);
      double[] size = sub.getImageSize();

      innerSvgs.add(svg);
      sizesW.add(size[0]);
      sizesH.add(size[1]);
    }

    List<DomContent> groupElements = new ArrayList<>();
    double currentY = 0;
    for (int i = 0; i < innerSvgs.size(); i++) {
      double w = sizesW.get(i);
      double h = sizesH.get(i);
      // Center horizontally
      double xOffset = (totalW - w) / 2.0;

      ContainerTag<?> g = tag("g")
          .attr("transform", String.format(Locale.US, "translate(%.2f, %.2f)", xOffset, currentY))
          .with(rawHtml(innerSvgs.get(i)));
      groupElements.add(g);
      currentY += h + gap;
    }

    // Draw Grid Frames if requested (e.g., Frame -> All)
    if (options.frame) {
      String frameStyle = "stroke:black;stroke-width:1px;fill:none;";

      // Outer Rectangle
      groupElements.add(tag("rect").attr("x", "0").attr("y", "0").attr("width", fmt(totalW))
          .attr("height", fmt(totalH)).attr("style", frameStyle));

      // Horizontal lines dividing the rows
      for (int i = 0; i < n - 1; i++) {
        double y = (i + 1) * (itemH + gap) - (gap > 0 ? gap / 2.0 : 0);
        groupElements.add(tag("line").attr("x1", "0").attr("y1", fmt(y)).attr("x2", fmt(totalW))
            .attr("y2", fmt(y)).attr("style", frameStyle));
      }
    }

    if (withSVGTag) {
      return tag("svg").attr("xmlns", "http://www.w3.org/2000/svg").attr("width", fmt(totalW))
          .attr("height", fmt(totalH)).attr("style", "max-width: 100%; height: auto;")
          .attr("viewBox", String.format(Locale.US, "0 0 %.0f %.0f", totalW, totalH))
          .with(groupElements).render();
    } else {
      return groupElements.stream().map(DomContent::render).collect(Collectors.joining("\n"));
    }
  }

  private String toSVGGrid(IAST graphicsExpr, boolean withSVGTag) {
    IAST grid = graphicsExpr;
    // Extract the inner list and parse GraphicsGrid options (like ImageSize, Frame)
    if (graphicsExpr.isAST(S.GraphicsGrid)) {
      if (graphicsExpr.argSize() >= 1 && graphicsExpr.arg1().isList()) {
        grid = (IAST) graphicsExpr.arg1();
      } else {
        return "";
      }
      parseOptions(graphicsExpr);
    }

    int rows = grid.argSize();
    if (rows == 0)
      return "";
    int maxCols = 0;

    // Determine maximum columns in the grid
    for (int r = 1; r <= rows; r++) {
      IExpr row = grid.get(r);
      if (row.isList()) {
        maxCols = Math.max(maxCols, row.argSize());
      }
    }
    if (maxCols == 0)
      return "";

    // Expand the total SVG canvas dynamically based on grid dimensions if not overridden
    // by constructor (SVGGraphics(w, h)) or by explicit ImageSize option.
    if (!imageSizeSet) {
      options.imageSize[0] = maxCols * 200.0;
      options.imageSize[1] = rows * 200.0;
    }

    double gap = 0.0; // GraphicsGrid typically places items flush against each other
    double totalW = options.imageSize[0];
    double totalH = options.imageSize[1];

    // Distribute total width and height equally among the items
    double itemW = (totalW - gap * (maxCols - 1)) / maxCols;
    double itemH = (totalH - gap * (rows - 1)) / rows;

    List<DomContent> groupElements = new ArrayList<>();

    // Process each child graphic using the scaled dimensions
    for (int r = 1; r <= rows; r++) {
      IExpr rowExpr = grid.get(r);
      if (!rowExpr.isList())
        continue;
      IAST rowList = (IAST) rowExpr;

      for (int c = 1; c <= rowList.argSize(); c++) {
        IExpr expr = rowList.get(c);
        // Skip common spanning placeholders to avoid crashes; they render as empty cells
        if (expr.isNone() || expr == S.SpanFromLeft || expr == S.SpanFromAbove
            || expr == S.SpanFromBoth) {
          continue;
        }

        if (expr.isGraphicsObject()) {
          SVGGraphics sub = new SVGGraphics(itemW, itemH);
          String svg = sub.toSVG((IAST) expr, false);

          // Calculate Top-Left layout coordinate
          double xOffset = (c - 1) * (itemW + gap);
          double yOffset = (r - 1) * (itemH + gap);

          ContainerTag<?> g = tag("g")
              .attr("transform",
                  String.format(Locale.US, "translate(%.2f, %.2f)", xOffset, yOffset))
              .with(rawHtml(svg));
          groupElements.add(g);
        }
      }
    }

    // Draw Grid Frames if requested (e.g., Frame -> All)
    if (options.frame) {
      String frameStyle = "stroke:black;stroke-width:1px;fill:none;";

      // Outer Rectangle
      groupElements.add(tag("rect").attr("x", "0").attr("y", "0").attr("width", fmt(totalW))
          .attr("height", fmt(totalH)).attr("style", frameStyle));

      // Vertical lines dividing the columns
      for (int c = 1; c < maxCols; c++) {
        double x = c * (itemW + gap) - (gap > 0 ? gap / 2.0 : 0);
        groupElements.add(tag("line").attr("x1", fmt(x)).attr("y1", "0").attr("x2", fmt(x))
            .attr("y2", fmt(totalH)).attr("style", frameStyle));
      }

      // Horizontal lines dividing the rows
      for (int r = 1; r < rows; r++) {
        double y = r * (itemH + gap) - (gap > 0 ? gap / 2.0 : 0);
        groupElements.add(tag("line").attr("x1", "0").attr("y1", fmt(y)).attr("x2", fmt(totalW))
            .attr("y2", fmt(y)).attr("style", frameStyle));
      }
    }

    if (withSVGTag) {
      return tag("svg").attr("xmlns", "http://www.w3.org/2000/svg").attr("width", fmt(totalW))
          .attr("height", fmt(totalH)).attr("style", "max-width: 100%; height: auto;")
          .attr("viewBox", String.format(Locale.US, "0 0 %.0f %.0f", totalW, totalH))
          .with(groupElements).render();
    } else {
      return groupElements.stream().map(DomContent::render).collect(Collectors.joining("\n"));
    }
  }

  private String toSVGRow(IAST graphicsExpr, boolean withSVGTag) {
    IAST items = graphicsExpr;
    // Extract the inner list and parse GraphicsRow options
    if (graphicsExpr.isAST(S.GraphicsRow)) {
      if (graphicsExpr.argSize() >= 1 && graphicsExpr.arg1().isList()) {
        items = (IAST) graphicsExpr.arg1();
      } else {
        return "";
      }
      parseOptions(graphicsExpr);
    }

    int n = items.argSize();
    if (n == 0)
      return "";

    double gap = 20.0;
    double totalW = options.imageSize[0];
    double totalH = options.imageSize[1];

    // Distribute total width equally among the items, minus the gaps
    double itemW = (totalW - gap * (n - 1)) / n;
    double itemH = totalH;

    List<String> innerSvgs = new ArrayList<>();
    List<Double> sizesW = new ArrayList<>();
    List<Double> sizesH = new ArrayList<>();

    // Process each child graphic using the scaled dimensions
    for (int i = 1; i <= n; i++) {
      IExpr expr = items.get(i);
      SVGGraphics sub = new SVGGraphics(itemW, itemH);
      String svg = sub.toSVG((IAST) expr, false);
      double[] size = sub.getImageSize();

      innerSvgs.add(svg);
      sizesW.add(size[0]);
      sizesH.add(size[1]);
    }

    List<DomContent> groupElements = new ArrayList<>();
    double currentX = 0;
    for (int i = 0; i < innerSvgs.size(); i++) {
      double w = sizesW.get(i);
      double h = sizesH.get(i);
      // Center vertically
      double yOffset = (totalH - h) / 2.0;

      ContainerTag<?> g = tag("g")
          .attr("transform", String.format(Locale.US, "translate(%.2f, %.2f)", currentX, yOffset))
          .with(rawHtml(innerSvgs.get(i)));
      groupElements.add(g);
      currentX += w + gap;
    }
    // Draw Grid Frames if requested (e.g., Frame -> All)
    if (options.frame) {
      String frameStyle = "stroke:black;stroke-width:1px;fill:none;";

      // Outer Rectangle
      groupElements.add(tag("rect").attr("x", "0").attr("y", "0").attr("width", fmt(totalW))
          .attr("height", fmt(totalH)).attr("style", frameStyle));

      // Vertical lines dividing the columns
      for (int i = 0; i < n - 1; i++) {
        double x = (i + 1) * (itemW + gap) - (gap > 0 ? gap / 2.0 : 0);
        groupElements.add(tag("line").attr("x1", fmt(x)).attr("y1", "0").attr("x2", fmt(x))
            .attr("y2", fmt(totalH)).attr("style", frameStyle));
      }
    }
    if (withSVGTag) {
      return tag("svg").attr("xmlns", "http://www.w3.org/2000/svg").attr("width", fmt(totalW))
          .attr("height", fmt(totalH)).attr("style", "max-width: 100%; height: auto;")
          .attr("viewBox", String.format(Locale.US, "0 0 %.0f %.0f", totalW, totalH))
          .with(groupElements).render();
    } else {
      return groupElements.stream().map(DomContent::render).collect(Collectors.joining("\n"));
    }
  }

  private void resetBounds() {
    dataMinX = Double.MAX_VALUE;
    dataMaxX = -Double.MAX_VALUE;
    dataMinY = Double.MAX_VALUE;
    dataMaxY = -Double.MAX_VALUE;
    allYValues.clear();
  }

  private void calculateViewport() {
    DoubleUnaryOperator tx = GraphicsOptions.getScalingFunction(options.scalingX);
    DoubleUnaryOperator ty = GraphicsOptions.getScalingFunction(options.scalingY);
    double rawMinX = dataMinX, rawMaxX = dataMaxX, rawMinY = dataMinY, rawMaxY = dataMaxY;
    if (options.plotRange != null) {
      if (!Double.isNaN(options.plotRange[0][0]))
        rawMinX = options.plotRange[0][0];
      if (!Double.isNaN(options.plotRange[0][1]))
        rawMaxX = options.plotRange[0][1];
      if (!Double.isNaN(options.plotRange[1][0]))
        rawMinY = options.plotRange[1][0];
      if (!Double.isNaN(options.plotRange[1][1]))
        rawMaxY = options.plotRange[1][1];
    }
    mapMinX = tx.applyAsDouble(rawMinX);
    mapMaxX = tx.applyAsDouble(rawMaxX);
    mapMinY = ty.applyAsDouble(rawMinY);
    mapMaxY = ty.applyAsDouble(rawMaxY);
    double rangeX = mapMaxX - mapMinX;
    double rangeY = mapMaxY - mapMinY;
    double px = rangeX * options.plotRangePadding;
    double py = rangeY * options.plotRangePadding;
    if (Math.abs(rangeX) < 1e-15)
      px = 0.5;
    if (Math.abs(rangeY) < 1e-15)
      py = 0.5;
    mapMinX -= px;
    mapMaxX += px;
    mapMinY -= py;
    mapMaxY += py;
    rangeX = mapMaxX - mapMinX;
    rangeY = mapMaxY - mapMinY;
    double drawingWidth = options.imageSize[0] - paddingLeft - paddingRight;
    double drawingHeight = options.imageSize[1] - paddingTop - paddingBottom;
    double targetRatio;

    if (options.aspectRatio == Double.POSITIVE_INFINITY)
      targetRatio = rangeY / rangeX;
    else if (!Double.isNaN(options.aspectRatio))
      targetRatio = options.aspectRatio;
    else {
      targetRatio = rangeY / rangeX;
    }

    double effectiveW = drawingWidth;
    double effectiveH = drawingHeight;
    double screenRatio = drawingHeight / drawingWidth;

    if (targetRatio > screenRatio) {
      effectiveH = drawingHeight;
      effectiveW = effectiveH / targetRatio;
    } else {
      effectiveW = drawingWidth;
      effectiveH = effectiveW * targetRatio;
    }
    scaleX = effectiveW / rangeX;
    scaleY = effectiveH / rangeY;
  }

  private double mapX(double x) {
    DoubleUnaryOperator tx = GraphicsOptions.getScalingFunction(options.scalingX);
    if (isLog(options.scalingX) && x <= 0.0)
      x = LOG_MIN_CLAMP;
    double val = tx.applyAsDouble(x);
    return paddingLeft + (val - mapMinX) * scaleX;
  }


  private double mapY(double y) {
    DoubleUnaryOperator ty = GraphicsOptions.getScalingFunction(options.scalingY);
    if (isLog(options.scalingY) && y <= 0.0)
      y = LOG_MIN_CLAMP;
    double val = ty.applyAsDouble(y);
    return (options.imageSize[1] - paddingBottom) - (val - mapMinY) * scaleY;
  }

  private boolean isBarLegend(IExpr expr) {
    return expr.isAST(S.BarLegend);
  }

  private boolean isLog(String scale) {
    return scale != null && (scale.equalsIgnoreCase("Log") || scale.equalsIgnoreCase("Log10"));
  }


  private List<Double> getNiceLogTicks(double min, double max) {
    List<Double> ticks = new ArrayList<>();
    if (min <= 0)
      min = LOG_MIN_CLAMP;
    if (max <= min)
      return ticks;

    double logMin = Math.log10(min);
    double logMax = Math.log10(max);
    double span = logMax - logMin;

    int startPow = (int) Math.floor(logMin);
    int endPow = (int) Math.ceil(logMax);

    if (span >= 3.0) {
      int stride = 1;
      if (span >= 8.0) {
        stride = (int) Math.ceil(span / 8.0);
      }
      for (int p = startPow; p <= endPow; p++) {
        if (Math.abs(p) % stride == 0) {
          double val = Math.pow(10, p);
          if (val >= min && val <= max) {
            ticks.add(val);
          }
        }
      }
    } else {
      for (int p = startPow; p <= endPow; p++) {
        double base = Math.pow(10, p);
        double[] steps = {1.0, 2.0, 5.0};
        for (double s : steps) {
          double val = s * base;
          if (val >= min && val <= max) {
            ticks.add(val);
          }
        }
      }
    }

    Collections.sort(ticks);
    return ticks;
  }

  private String getLogTickLabel(double val) {
    double log10 = Math.log10(val);
    double roundLog = Math.round(log10);
    boolean isPowerOf10 = Math.abs(log10 - roundLog) < 1e-9;

    if (isPowerOf10) {
      long exponent = (long) roundLog;
      if (exponent >= 3 || exponent <= -3) {
        return String.format(Locale.US, "10<tspan dy=\"-0.6em\" font-size=\"70%%\">%d</tspan>",
            exponent);
      }
    }
    return fmt(val);
  }

  private double[] calculateTicks(double min, double max) {
    double range = niceNum(max - min, false);
    double tickSpacing = niceNum(range / 5.0, true);
    double firstTick = Math.ceil(min / tickSpacing) * tickSpacing;
    List<Double> ticks = new ArrayList<>();
    for (double t = firstTick; t <= max; t += tickSpacing)
      ticks.add(t);
    return ticks.stream().mapToDouble(d -> d).toArray();
  }

  private double niceNum(double range, boolean round) {
    double exponent = Math.floor(Math.log10(range));
    double fraction = range / Math.pow(10, exponent);
    double niceFraction;
    if (round) {
      if (fraction < 1.5)
        niceFraction = 1;
      else if (fraction < 3)
        niceFraction = 2;
      else if (fraction < 7)
        niceFraction = 5;
      else
        niceFraction = 10;
    } else {
      if (fraction <= 1)
        niceFraction = 1;
      else if (fraction <= 2)
        niceFraction = 2;
      else if (fraction <= 5)
        niceFraction = 5;
      else
        niceFraction = 10;
    }
    return niceFraction * Math.pow(10, exponent);
  }

  private String fmt(double d) {
    if (Math.abs(d - Math.round(d)) < 1e-9)
      return String.format("%d", Math.round(d));
    return String.format(Locale.US, "%.3f", d);
  }

  private void processSymbol(IBuiltInSymbol sym, GraphicState state) {
    switch (sym.ordinal()) {
      case ID.Red:
        setColor(Color.RED, state);
        break;
      case ID.Green:
        setColor(Color.GREEN, state);
        break;
      case ID.Blue:
        setColor(Color.BLUE, state);
        break;
      case ID.Black:
        setColor(Color.BLACK, state);
        break;
      case ID.White:
        setColor(Color.WHITE, state);
        break;
      case ID.Gray:
        setColor(Color.GRAY, state);
        break;
      case ID.Yellow:
        setColor(Color.YELLOW, state);
        break;
      case ID.Cyan:
        setColor(Color.CYAN, state);
        break;
      case ID.Magenta:
        setColor(Color.MAGENTA, state);
        break;
      case ID.Orange:
        setColor(Color.ORANGE, state);
        break;
      case ID.Pink:
        setColor(Color.PINK, state);
        break;
      case ID.Purple:
        setColor(new Color(128, 0, 128), state);
        break;
      case ID.Brown:
        setColor(new Color(165, 42, 42), state);
        break;
      case ID.Dashed:
        state.dashArray = "6,4";
        break;
      case ID.Dotted:
        state.dashArray = "1,3";
        break;
      case ID.Thick:
        state.strokeWidth = options.imageSize[0] * 0.006;
        break;
      case ID.Thin:
        state.strokeWidth = options.imageSize[0] * 0.001;
        break;
    }
  }

  private void setColor(Color c, GraphicState state) {
    state.strokeColor = c;
    state.fillColor = c;
  }

  private void updateColor(IAST ast, GraphicState state) {
    Color c = parseColor(ast);
    state.strokeColor = c;
    state.fillColor = c;
    IExpr head = ast.head();
    if (head.isBuiltInSymbol()) {
      int ord = ((IBuiltInSymbol) head).ordinal();
      boolean hasAlpha = false;
      if (ord == ID.RGBColor && ast.argSize() >= 4)
        hasAlpha = true;
      else if (ord == ID.Hue && ast.argSize() >= 4)
        hasAlpha = true;
      else if (ord == ID.GrayLevel && ast.argSize() >= 2)
        hasAlpha = true;
      else if (ord == ID.CMYKColor && ast.argSize() >= 5)
        hasAlpha = true;

      if (hasAlpha) {
        state.opacity = c.getAlpha() / 255.0;
        state.edgeOpacity = state.opacity;
      }
    }
  }

  private void updateFaceForm(IAST ast, GraphicState state) {
    IExpr arg = ast.arg1();
    if (arg.isNone())
      state.faceColor = new Color(0, 0, 0, 0);
    else
      state.faceColor = parseColor(arg);
  }

  private void updateEdgeForm(IAST ast, GraphicState state) {
    state.edgeFormSet = true;
    IExpr arg = ast.arg1();
    if (arg.isList()) {
      for (IExpr e : (IAST) arg) {
        if (e.isAST()) {
          ISymbol head = e.topHead();
          if (head.isBuiltInSymbol()) {
            int ord = head.ordinal();
            if (ord == ID.RGBColor || ord == ID.Hue || ord == ID.GrayLevel || ord == ID.CMYKColor) {
              Color c = parseColor(e);
              state.edgeColor = c;
              state.edgeOpacity = c.getAlpha() / 255.0;
            } else if (ord == ID.Opacity) {
              state.edgeOpacity = getDouble(((IAST) e).arg1());
            } else if (ord == ID.Thickness) {
              state.strokeWidth = getDouble(((IAST) e).arg1()) * options.imageSize[0];
            } else if (ord == ID.AbsoluteThickness) {
              state.strokeWidth = getDouble(((IAST) e).arg1());
            }
          }
        } else if (e.isBuiltInSymbol()) {
          if (e.equals(S.Thick))
            state.strokeWidth = options.imageSize[0] * 0.006;
        }
      }
    } else if (arg.equals(F.None)) {
      state.edgeColor = null;
    } else {
      state.edgeColor = parseColor(arg);
      if (state.edgeColor != null) {
        state.edgeOpacity = state.edgeColor.getAlpha() / 255.0;
      }
    }
  }

  private void updateDashingAST(IAST ast, GraphicState state, boolean relative) {
    IExpr arg = ast.arg1();
    if (arg.isList()) {
      StringBuilder sb = new StringBuilder();
      IAST list = (IAST) arg;
      double factor = relative ? options.imageSize[0] : 1.0;
      for (int i = 1; i <= list.argSize(); i++) {
        IExpr elem = list.get(i);
        double val = 0.0;
        if (elem.isBuiltInSymbol()) {
          switch (((IBuiltInSymbol) elem).ordinal()) {
            case ID.Small:
              val = 0.01;
              break;
            case ID.Medium:
              val = 0.02;
              break;
            case ID.Large:
              val = 0.04;
              break;
            case ID.Tiny:
              val = 0.005;
              break;
            default:
              val = 0.02;
          }
        } else {
          val = getDouble(elem);
        }
        if (i > 1)
          sb.append(",");
        sb.append(String.format(Locale.US, "%.2f", val * factor));
      }
      state.dashArray = sb.toString();
    } else if (arg.isNone()) {
      state.dashArray = "none";
    }
  }

  private void updateArrowheads(IAST ast, GraphicState state) {
    IExpr arg = ast.arg1();
    if (arg.isBuiltInSymbol()) {
      switch (((IBuiltInSymbol) arg).ordinal()) {
        case ID.Tiny:
          state.arrowHeadScale = 0.02;
          break;
        case ID.Small:
          state.arrowHeadScale = 0.03;
          break;
        case ID.Medium:
          state.arrowHeadScale = 0.05;
          break;
        case ID.Large:
          state.arrowHeadScale = 0.08;
          break;
        default:
          state.arrowHeadScale = 0.05;
      }
    } else {
      state.arrowHeadScale = getDouble(arg, 0.05);
    }
  }

  private void updatePointSize(IAST ast, GraphicState state) {
    IExpr arg = ast.arg1();
    double scale = 0.015; // default
    if (arg.isBuiltInSymbol()) {
      switch (((IBuiltInSymbol) arg).ordinal()) {
        case ID.Tiny:
          scale = 0.005;
          break;
        case ID.Small:
          scale = 0.01;
          break;
        case ID.Medium:
          scale = 0.015;
          break;
        case ID.Large:
          scale = 0.03;
          break;
        default:
          scale = 0.015;
      }
    } else {
      scale = getDouble(arg, 0.015);
    }
    state.pointRadius = (scale * options.imageSize[0]) / 2.0;
  }

  private void updateCapForm(IAST ast, GraphicState state) {
    String cap = ast.arg1().toString().toLowerCase();
    if (cap.contains("round"))
      state.lineCap = "round";
    else if (cap.contains("square"))
      state.lineCap = "square";
    else
      state.lineCap = "butt";
  }

  private void updateJoinForm(IAST ast, GraphicState state) {
    String join = ast.arg1().toString().toLowerCase();
    if (join.contains("round"))
      state.lineJoin = "round";
    else if (join.contains("bevel"))
      state.lineJoin = "bevel";
    else
      state.lineJoin = "miter";
  }

  private void processElement(IExpr expr, GraphicState state, ContainerTag<?> parent) {
    if (expr.isList()) {
      IAST list = (IAST) expr;
      GraphicState scope = state.clone();
      ContainerTag<?> g = tag("g");
      for (int i = 1; i <= list.argSize(); i++) {
        processElement(list.get(i), scope, g);
      }
      parent.with(g);
      return;
    }
    if (expr.isBuiltInSymbol()) {
      processSymbol((IBuiltInSymbol) expr, state);
      return;
    }
    if (!expr.isAST())
      return;
    IAST ast = (IAST) expr;
    IExpr head = ast.head();
    if (head.isBuiltInSymbol()) {
      switch (((IBuiltInSymbol) head).ordinal()) {
        case ID.RGBColor:
        case ID.Hue:
        case ID.GrayLevel:
        case ID.CMYKColor:
          updateColor(ast, state);
          break;
        case ID.Opacity:
          state.opacity = getDouble(ast.arg1(), 1.0);
          state.edgeOpacity = state.opacity;
          break;
        case ID.Thickness:
          state.strokeWidth = getDouble(ast.arg1(), 0.001) * options.imageSize[0];
          break;
        case ID.AbsoluteThickness:
          state.strokeWidth = getDouble(ast.arg1(), 1.0);
          break;
        case ID.PointSize:
          updatePointSize(ast, state);
          break;
        case ID.AbsolutePointSize:
          state.pointRadius = getDouble(ast.arg1(), 3.0);
          break;
        case ID.Arrowheads:
          updateArrowheads(ast, state);
          break;
        case ID.EdgeForm:
          updateEdgeForm(ast, state);
          break;
        case ID.FaceForm:
          updateFaceForm(ast, state);
          break;
        case ID.Dashing:
          updateDashingAST(ast, state, true);
          break;
        case ID.AbsoluteDashing:
          updateDashingAST(ast, state, false);
          break;
        case ID.CapForm:
          updateCapForm(ast, state);
          break;
        case ID.JoinForm:
          updateJoinForm(ast, state);
          break;
        case ID.Directive:
          for (int i = 1; i <= ast.argSize(); i++)
            processElement(ast.get(i), state, parent);
          break;
        case ID.Style:
          processStyle(ast, state, parent);
          break;
        case ID.GraphicsComplex:
          processGraphicsComplex(ast, state, parent);
          break;
        case ID.GraphicsGroup:
        case ID.Annotation:
        case ID.Tooltip:
        case ID.Mouseover:
        case ID.StatusArea:
        case ID.Legended:
          if (ast.argSize() >= 1) {
            processElement(ast.arg1(), state, parent);
          }
          break;
        case ID.Line:
          drawLine(ast, state, parent);
          break;
        case ID.Point:
          drawPoint(ast, state, parent);
          break;
        case ID.Rectangle:
          drawRectangle(ast, state, parent);
          break;
        case ID.Polygon:
          drawPolygon(ast, state, parent);
          break;
        case ID.Circle:
          drawCircleOrDisk(ast, state, false, parent);
          break;
        case ID.Disk:
          if (ast.argSize() >= 3)
            drawDiskSegment(ast, state, parent);
          else
            drawCircleOrDisk(ast, state, true, parent);
          break;
        case ID.Text:
          drawText(ast, state, parent);
          break;
        case ID.Inset:
          drawInset(ast, state, parent);
          break;
        case ID.Arrow:
          drawArrow(ast, state, parent);
          break;
        case ID.BezierCurve:
          drawBezierCurve(ast, state, parent);
          break;
        case ID.BSplineCurve:
          drawBSplineCurve(ast, state, parent);
          break;
        case ID.Parallelogram:
          drawParallelogram(ast, state, parent);
          break;
        case ID.Triangle:
          drawPolygon(ast, state, parent);
          break;
        case ID.SSSTriangle:
          drawTriangleSSS(ast, state, parent);
          break;
        case ID.SASTriangle:
          drawTriangleSAS(ast, state, parent);
          break;
        case ID.ASATriangle:
          drawTriangleASA(ast, state, parent);
          break;
        case ID.AASTriangle:
          drawTriangleAAS(ast, state, parent);
          break;
        case ID.Rule:
          processRule(ast, state);
          break;
      }
    }
  }

  private void processRule(IAST ast, GraphicState state) {
    IExpr key = ast.arg1();
    IExpr val = ast.arg2();
    if (key.isBuiltInSymbol()) {
      switch (((IBuiltInSymbol) key).ordinal()) {
        case ID.FontColor:
          Color c = toColor(val);
          if (c != null) {
            state.strokeColor = c;
            state.fillColor = c;
          }
          break;
        case ID.FontSize:
          if (val.isAST(S.Scaled, 2)) {
            // Apply scale proportionately to the main horizontal image capacity
            state.fontSize = getDouble(((IAST) val).arg1(), 0.05) * options.imageSize[0];
          } else if (val.isAST(S.Offset, 2)) {
            state.fontSize = getDouble(((IAST) val).arg1(), 12.0);
          } else {
            double size = getDouble(val, 12.0);
            if (val.isBuiltInSymbol()) {
              switch (((IBuiltInSymbol) val).ordinal()) {
                case ID.Tiny:
                  size = 8.0;
                  break;
                case ID.Small:
                  size = 10.0;
                  break;
                case ID.Medium:
                  size = 12.0;
                  break;
                case ID.Large:
                  size = 18.0;
                  break;
              }
            }
            state.fontSize = size;
          }
          break;
        case ID.FontFamily:
          state.fontFamily = val.toString().replace("\"", "");
          break;
      }
    }
  }
  private void processStyle(IAST ast, GraphicState state, ContainerTag<?> parent) {
    GraphicState styleState = state.clone();
    for (int i = 2; i <= ast.argSize(); i++) {
      parseStyle(ast.get(i), styleState);
    }
    processElement(ast.arg1(), styleState, parent);
  }

  private void processGraphicsComplex(IAST ast, GraphicState state, ContainerTag<?> parent) {
    if (ast.arg1().isList()) {
      GraphicState complexState = state.clone();
      complexState.complexVertices = new ArrayList<>();
      IAST pts = (IAST) ast.arg1();
      for (int i = 1; i <= pts.argSize(); i++) {
        complexState.complexVertices.add(parsePointRaw(pts.get(i)));
      }
      if (ast.argSize() >= 2)
        processElement(ast.arg2(), complexState, parent);
    }
  }

  private void drawLine(IAST ast, GraphicState state, ContainerTag<?> parent) {
    if (isMultiSegment(ast.arg1(), state)) {
      for (IExpr segment : (IAST) ast.arg1()) {
        drawPath(extractPoints(segment, state), state, false, parent);
      }
    } else {
      drawPath(extractPoints(ast.arg1(), state), state, false, parent);
    }
  }

  private void drawPath(List<double[]> points, GraphicState state, boolean close,
      ContainerTag<?> parent) {
    if (points.isEmpty())
      return;
    StringBuilder d = new StringBuilder();
    boolean pending = true;
    boolean hasPoints = false;
    for (double[] p : points) {
      double sx = mapX(p[0]), sy = mapY(p[1]);
      if (Double.isFinite(sx) && Double.isFinite(sy)) {
        if (pending) {
          d.append(String.format(Locale.US, "M %.2f %.2f ", sx, sy));
          pending = false;
        } else
          d.append(String.format(Locale.US, "L %.2f %.2f ", sx, sy));
        hasPoints = true;
      } else
        pending = true;
    }
    if (close)
      d.append("Z ");
    if (hasPoints) {
      ContainerTag<?> path = tag("path").attr("d", d.toString());
      applyState(path, state, null, "none");
      parent.with(path);
    }
  }

  private void drawRectangle(IAST ast, GraphicState state, ContainerTag<?> parent) {
    double[] p1 = parsePoint(ast.arg1(), state);
    double[] p2 =
        (ast.argSize() > 1) ? parsePoint(ast.arg2(), state) : new double[] {p1[0] + 1, p1[1] + 1};
    double x1 = mapX(Math.min(p1[0], p2[0]));
    double y1 = mapY(Math.max(p1[1], p2[1]));
    double w = Math.abs(mapX(p2[0]) - mapX(p1[0]));
    double h = Math.abs(mapY(p2[1]) - mapY(p1[1]));

    String stroke = null;
    if (state.edgeFormSet && state.edgeColor != null) {
      stroke = colorToCss(state.edgeColor);
    } else if (state.getEffectiveFillColor().getAlpha() == 0 && state.strokeColor != null) {
      stroke = colorToCss(state.strokeColor);
    } else {
      stroke = "none";
    }

    ContainerTag<?> rect = tag("rect").attr("x", fmt(x1)).attr("y", fmt(y1)).attr("width", fmt(w))
        .attr("height", fmt(h));
    applyState(rect, state, stroke, null);
    if ("none".equals(stroke) && state.getEffectiveFillColor().getAlpha() > 0) {
      rect.attr("shape-rendering", "crispEdges");
    }
    parent.with(rect);
  }

  private void drawPoint(IAST ast, GraphicState state, ContainerTag<?> parent) {
    List<double[]> points = extractPoints(ast.arg1(), state);
    String fill = colorToCss(state.strokeColor);
    for (double[] p : points) {
      double sx = mapX(p[0]), sy = mapY(p[1]);
      if (Double.isFinite(sx) && Double.isFinite(sy)) {
        ContainerTag<?> circle =
            tag("circle").attr("cx", fmt(sx)).attr("cy", fmt(sy)).attr("r", fmt(state.pointRadius));
        applyState(circle, state, "none", fill);
        parent.with(circle);
      }
    }
  }

  private void drawText(IAST ast, GraphicState state, ContainerTag<?> parent) {
    IExpr content = ast.arg1();
    GraphicState textState = state.clone();

    boolean isFramed = false;
    Color boxFrameColor = Color.BLACK;
    Color boxBackgroundColor = null;

    while (content.isAST()) {
      IAST wrapper = (IAST) content;
      IExpr head = wrapper.head();
      if (head.equals(S.Framed)) {
        isFramed = true;
        IExpr bg = extractOption(wrapper, S.Background);
        if (bg != null) {
          boxBackgroundColor = toColor(bg);
        }

        IExpr fs = extractOption(wrapper, S.FrameStyle);
        if (fs != null) {
          Color c = toColor(fs);
          if (c != null) {
            boxFrameColor = c;
          }
        }
        content = wrapper.arg1();
      } else if (head.equals(S.Style)) {
        // Parse the embedded styles dynamically into the text graphics state
        for (int i = 2; i <= wrapper.argSize(); i++) {
          parseStyle(wrapper.get(i), textState);
        }
        content = wrapper.arg1();
      } else {
        break;
      }
    }

    String txt = content.toString().replace("\"", "");
    double[] pos = parsePoint(ast.arg2(), state);
    double sx = mapX(pos[0]);
    double sy = mapY(pos[1]);
    double ox = 0.0, oy = 0.0;
    if (ast.argSize() >= 3) {
      IExpr offsetExpr = ast.arg3();
      if (offsetExpr.isList()) {
        ox = getDouble(((IAST) offsetExpr).arg1(), 0.0);
        oy = getDouble(((IAST) offsetExpr).arg2(), 0.0);
      }
    }

    // Scale text bounds based on updated state explicitly rather than global state
    double w = txt.length() * textState.fontSize * 0.6;
    double h = textState.fontSize;
    double shiftX = -ox * (w / 2.0);
    double shiftY = oy * (h / 2.0);

    if (Double.isFinite(sx) && Double.isFinite(sy)) {
      if (isFramed) {
        double padding = 4.0;
        double rectW = w + 2 * padding;
        double rectH = h + padding;
        double rectX = sx + shiftX - rectW / 2.0;
        double rectY = sy + shiftY - h / 2.0 - padding;

        String fill = "none";
        if (boxBackgroundColor != null) {
          fill = colorToCss(boxBackgroundColor);
        }
        String stroke = colorToCss(boxFrameColor);

        parent
            .with(tag("rect").attr("x", fmt(rectX)).attr("y", fmt(rectY)).attr("width", fmt(rectW))
                .attr("height", fmt(rectH)).attr("fill", fill).attr("stroke", stroke));
      }

      parent.with(tag("text").attr("x", fmt(sx)).attr("y", fmt(sy))
          .attr("fill", colorToCss(textState.strokeColor)).attr("font-family", textState.fontFamily)
          .attr("font-size", fmt(textState.fontSize)).attr("text-anchor", "middle")
          .attr("transform", String.format(Locale.US, "translate(%.2f, %.2f)", shiftX, shiftY))
          .withText(txt));
    }
  }

  private void drawPolygon(IAST ast, GraphicState state, ContainerTag<?> parent) {
    // Check if the argument represents multiple polygons: {{pt...}, {pt...}}
    if (isMultiSegment(ast.arg1(), state)) {
      // Iterate through the list of polygons
      for (IExpr singlePolygonPts : (IAST) ast.arg1()) {
        drawSinglePolygon(singlePolygonPts, state, parent);
      }
    } else {
      // Standard case: Single Polygon {pt1, pt2, ...}
      drawSinglePolygon(ast.arg1(), state, parent);
    }
  }

  private void drawSinglePolygon(IExpr coords, GraphicState state, ContainerTag<?> parent) {
    List<double[]> points = extractPoints(coords, state);
    drawPolygonPoints(state, parent, points.toArray(new double[0][]));
  }

  private void drawPolygonPoints(GraphicState state, ContainerTag<?> parent, double[]... points) {
    if (points.length == 0)
      return;
    StringBuilder sb = new StringBuilder();
    for (double[] p : points) {
      sb.append(fmt(mapX(p[0]))).append(",").append(fmt(mapY(p[1]))).append(" ");
    }

    String stroke = null;
    if (state.edgeFormSet && state.edgeColor != null) {
      stroke = colorToCss(state.edgeColor);
    } else {
      stroke = "none";
    }

    ContainerTag<?> poly = tag("polygon").attr("points", sb.toString().trim());
    applyState(poly, state, stroke, null);
    parent.with(poly);
  }

  private void drawCircleOrDisk(IAST ast, GraphicState state, boolean filled,
      ContainerTag<?> parent) {
    double[] c = parsePoint(ast.arg1(), state);
    double r = getDouble(ast, 2, 1.0);
    double rx = r * scaleX;
    double ry = r * scaleY;
    String fill = filled ? null : "none";
    String stroke = "none";
    if (filled && state.edgeFormSet && state.edgeColor != null) {
      stroke = colorToCss(state.edgeColor);
    } else if (!filled) {
      stroke = colorToCss(state.strokeColor);
    }
    ContainerTag<?> ellipse = tag("ellipse").attr("cx", fmt(mapX(c[0]))).attr("cy", fmt(mapY(c[1])))
        .attr("rx", fmt(rx)).attr("ry", fmt(ry));
    applyState(ellipse, state, stroke, fill);
    parent.with(ellipse);
  }

  private void drawDiskSegment(IAST ast, GraphicState state, ContainerTag<?> parent) {
    double[] c = parsePoint(ast.arg1(), state);
    double r = getDouble(ast, 2, 1.0);
    IExpr angleSpec = ast.arg3();
    double t1 = 0, t2 = 2 * Math.PI;
    if (angleSpec.isList() && angleSpec.size() >= 3) {
      IAST angs = (IAST) angleSpec;
      t1 = getDouble(angs.arg1(), 0.0);
      t2 = getDouble(angs.arg2(), 0.0);
    }
    int steps = 50;
    double dt = (t2 - t1) / steps;
    StringBuilder sb = new StringBuilder();
    sb.append(fmt(mapX(c[0]))).append(",").append(fmt(mapY(c[1]))).append(" ");
    for (int i = 0; i <= steps; i++) {
      double theta = t1 + i * dt;
      double px = c[0] + r * Math.cos(theta);
      double py = c[1] + r * Math.sin(theta);
      sb.append(fmt(mapX(px))).append(",").append(fmt(mapY(py))).append(" ");
    }
    String stroke = null;
    if (state.edgeFormSet && state.edgeColor != null) {
      stroke = colorToCss(state.edgeColor);
    } else {
      stroke = "none";
    }
    ContainerTag<?> poly = tag("polygon").attr("points", sb.toString().trim());
    applyState(poly, state, stroke, null);
    parent.with(poly);
  }

  private void drawArrow(IAST ast, GraphicState state, ContainerTag<?> parent) {
    if (isMultiSegment(ast.arg1(), state)) {
      for (IExpr segment : (IAST) ast.arg1()) {
        drawSingleArrow(segment, state, parent);
      }
    } else {
      drawSingleArrow(ast.arg1(), state, parent);
    }
  }

  private void drawSingleArrow(IExpr coords, GraphicState state, ContainerTag<?> parent) {
    List<double[]> points = extractPoints(coords, state);
    if (points.size() < 2)
      return;
    drawPath(points, state, false, parent);
    double[] lastP = points.get(points.size() - 1);
    double[] prevP = points.get(points.size() - 2);
    double xEnd = mapX(lastP[0]);
    double yEnd = mapY(lastP[1]);
    double xPrev = mapX(prevP[0]);
    double yPrev = mapY(prevP[1]);
    if (Double.isFinite(xEnd) && Double.isFinite(yEnd) && Double.isFinite(xPrev)
        && Double.isFinite(yPrev)) {
      double angle = Math.atan2(yEnd - yPrev, xEnd - xPrev);
      double hs = state.arrowHeadScale * options.imageSize[0];
      double x1 = xEnd - hs * Math.cos(angle - Math.PI / 6);
      double y1 = yEnd - hs * Math.sin(angle - Math.PI / 6);
      double x2 = xEnd - hs * Math.cos(angle + Math.PI / 6);
      double y2 = yEnd - hs * Math.sin(angle + Math.PI / 6);
      StringBuilder pts = new StringBuilder();
      pts.append(fmt(xEnd)).append(",").append(fmt(yEnd)).append(" ").append(fmt(x1)).append(",")
          .append(fmt(y1)).append(" ").append(fmt(x2)).append(",").append(fmt(y2));
      ContainerTag<?> poly = tag("polygon").attr("points", pts.toString());
      applyState(poly, state, "none", colorToCss(state.strokeColor));
      parent.with(poly);
    }
  }

  private void drawBezierCurve(IAST ast, GraphicState state, ContainerTag<?> parent) {
    if (isMultiSegment(ast.arg1(), state)) {
      for (IExpr segment : (IAST) ast.arg1()) {
        drawSingleBezierCurve(segment, ast, state, parent);
      }
    } else {
      drawSingleBezierCurve(ast.arg1(), ast, state, parent);
    }
  }

  private void drawSingleBezierCurve(IExpr coords, IAST ast, GraphicState state,
      ContainerTag<?> parent) {
    List<double[]> points = extractPoints(coords, state);
    if (points.isEmpty())
      return;
    StringBuilder d = new StringBuilder();
    double[] p0 = points.get(0);
    d.append(String.format(Locale.US, "M %.2f %.2f ", mapX(p0[0]), mapY(p0[1])));
    int degree = 3;
    if (ast.argSize() >= 2)
      degree = ast.arg2().toIntDefault(3);
    int i = 1;
    while (i < points.size()) {
      if (degree == 2) {
        if (i + 1 < points.size()) {
          double[] p1 = points.get(i);
          double[] p2 = points.get(i + 1);
          d.append(String.format(Locale.US, "Q %.2f %.2f %.2f %.2f ", mapX(p1[0]), mapY(p1[1]),
              mapX(p2[0]), mapY(p2[1])));
          i += 2;
        } else
          break;
      } else {
        if (i + 2 < points.size()) {
          double[] p1 = points.get(i);
          double[] p2 = points.get(i + 1);
          double[] p3 = points.get(i + 2);
          d.append(String.format(Locale.US, "C %.2f %.2f %.2f %.2f %.2f %.2f ", mapX(p1[0]),
              mapY(p1[1]), mapX(p2[0]), mapY(p2[1]), mapX(p3[0]), mapY(p3[1])));
          i += 3;
        } else {
          double[] pLast = points.get(i);
          d.append(String.format(Locale.US, "L %.2f %.2f ", mapX(pLast[0]), mapY(pLast[1])));
          i++;
        }
      }
    }
    ContainerTag<?> path = tag("path").attr("d", d.toString());
    applyState(path, state, null, "none");
    parent.with(path);
  }

  private void drawBSplineCurve(IAST ast, GraphicState state, ContainerTag<?> parent) {
    List<double[]> points = extractPoints(ast.arg1(), state);
    if (points.isEmpty()) {
      return;
    }

    int degree = 3;
    IExpr optDegree = extractOption(ast, S.SplineDegree);
    if (optDegree != null) {
      degree = optDegree.toIntDefault(3);
    }

    boolean closed = false;
    IExpr optClosed = extractOption(ast, S.SplineClosed);
    if (optClosed != null && optClosed.isTrue()) {
      closed = true;
    }

    double[] weights = null;
    IExpr optWeights = extractOption(ast, S.SplineWeights);
    if (optWeights != null && optWeights.isList()) {
      IAST wList = (IAST) optWeights;
      weights = new double[points.size()];
      int count = Math.min(weights.length, wList.size() - 1);
      for (int i = 0; i < count; i++) {
        weights[i] = wList.get(i + 1).evalf();
      }
      for (int i = count; i < weights.length; i++)
        weights[i] = 1.0;
    }

    double[] knots = null;
    IExpr optKnots = extractOption(ast, S.SplineKnots);
    if (optKnots != null && optKnots.isList()) {
      IAST kList = (IAST) optKnots;
      knots = new double[kList.size() - 1];
      for (int i = 0; i < knots.length; i++) {
        knots[i] = kList.get(i + 1).evalf();
      }
    }

    if (closed) {
      int n = points.size();
      for (int i = 0; i < degree; i++) {
        points.add(points.get(i % n));
      }
      if (weights != null) {
        double[] newWeights = new double[points.size()];
        System.arraycopy(weights, 0, newWeights, 0, n);
        for (int i = 0; i < degree; i++) {
          newWeights[n + i] = weights[i % n];
        }
        weights = newWeights;
      }
    }

    int n = points.size();
    if (knots == null) {
      int kCount = n + degree + 1;
      knots = new double[kCount];
      if (!closed) {
        for (int i = 0; i <= degree; i++)
          knots[i] = 0.0;
        for (int i = 1; i < n - degree; i++) {
          knots[degree + i] = i;
        }
        for (int i = n; i < kCount; i++)
          knots[i] = n - degree;
      } else {
        for (int i = 0; i < kCount; i++)
          knots[i] = i;
      }
    }

    int stepsPerSpan = 20;
    StringBuilder d = new StringBuilder();
    boolean first = true;

    double tStart = knots[degree];
    double tEnd = knots[n];

    int totalSteps = (int) ((tEnd - tStart) * stepsPerSpan);
    if (totalSteps < 20)
      totalSteps = 20;

    for (int i = 0; i <= totalSteps; i++) {
      double t = tStart + (tEnd - tStart) * i / totalSteps;
      if (t < tStart)
        t = tStart;
      if (t > tEnd - 1e-9)
        t = tEnd - 1e-9;

      double[] pt = deBoorEval(t, degree, knots, points, weights);
      if (first) {
        d.append(String.format(Locale.US, "M %.2f %.2f ", mapX(pt[0]), mapY(pt[1])));
        first = false;
      } else {
        d.append(String.format(Locale.US, "L %.2f %.2f ", mapX(pt[0]), mapY(pt[1])));
      }
    }

    if (closed)
      d.append("Z");

    ContainerTag<?> path = tag("path").attr("d", d.toString());
    applyState(path, state, null, "none");
    parent.with(path);
  }

  private double[] deBoorEval(double u, int degree, double[] knots, List<double[]> points,
      double[] weights) {
    int n = points.size();
    int k = -1;
    for (int i = degree; i < n; i++) {
      if (u >= knots[i] && u < knots[i + 1]) {
        k = i;
        break;
      }
    }
    if (k == -1)
      return points.get(points.size() - 1);

    double[][] d = new double[degree + 1][(weights != null) ? 3 : 2];
    for (int j = 0; j <= degree; j++) {
      int pIdx = k - degree + j;
      double[] p = points.get(pIdx);
      double w = (weights != null) ? weights[pIdx] : 1.0;
      d[j][0] = p[0] * w;
      d[j][1] = p[1] * w;
      if (weights != null)
        d[j][2] = w;
    }

    for (int r = 1; r <= degree; r++) {
      for (int j = degree; j >= r; j--) {
        double denom = knots[j + 1 + k - r] - knots[j + k - degree];
        double alpha = (denom == 0) ? 0.0 : (u - knots[j + k - degree]) / denom;
        for (int z = 0; z < ((weights != null) ? 3 : 2); z++) {
          d[j][z] = (1.0 - alpha) * d[j - 1][z] + alpha * d[j][z];
        }
      }
    }
    double rw = (weights != null) ? d[degree][2] : 1.0;
    return new double[] {d[degree][0] / rw, d[degree][1] / rw};
  }

  private IExpr extractOption(IAST ast, ISymbol optionName) {
    for (int i = 1; i < ast.size(); i++) {
      IExpr arg = ast.get(i);
      if (arg.isRuleAST() && arg.first().equals(optionName)) {
        return ((IAST) arg).second();
      }
    }
    return null;
  }


  private void drawParallelogram(IAST ast, GraphicState state, ContainerTag<?> parent) {
    if (ast.argSize() < 2)
      return;
    double[] origin = parsePoint(ast.arg1(), state);
    IExpr vectorsExpr = ast.arg2();
    if (vectorsExpr.isList() && vectorsExpr.size() >= 3) {
      double[] v1 = parsePoint(((IAST) vectorsExpr).get(1), state);
      double[] v2 = parsePoint(((IAST) vectorsExpr).get(2), state);
      double[] p1 = origin;
      double[] p2 = {origin[0] + v1[0], origin[1] + v1[1]};
      double[] p3 = {origin[0] + v1[0] + v2[0], origin[1] + v1[1] + v2[1]};
      double[] p4 = {origin[0] + v2[0], origin[1] + v2[1]};
      drawPolygonPoints(state, parent, p1, p2, p3, p4);
    }
  }

  private void drawTriangleSSS(IAST ast, GraphicState state, ContainerTag<?> parent) {
    double a = getDouble(ast.arg1());
    double b = getDouble(ast.arg2());
    double c = getDouble(ast.arg3());
    double x = (a * a + c * c - b * b) / (2 * a);
    double y = Math.sqrt(Math.max(0, c * c - x * x));
    drawPolygonPoints(state, parent, new double[] {0, 0}, new double[] {a, 0}, new double[] {x, y});
  }

  private void drawTriangleSAS(IAST ast, GraphicState state, ContainerTag<?> parent) {
    double a = getDouble(ast.arg1());
    double ang = getDouble(ast.arg2());
    double b = getDouble(ast.arg3());
    double x = b * Math.cos(ang);
    double y = b * Math.sin(ang);
    drawPolygonPoints(state, parent, new double[] {0, 0}, new double[] {a, 0}, new double[] {x, y});
  }

  private void drawTriangleASA(IAST ast, GraphicState state, ContainerTag<?> parent) {
    double alpha = getDouble(ast.arg1());
    double c = getDouble(ast.arg2());
    double beta = getDouble(ast.arg3());
    double tanA = Math.tan(alpha);
    double tanB = Math.tan(beta);
    double x = c * tanB / (tanA + tanB);
    double y = x * tanA;
    drawPolygonPoints(state, parent, new double[] {0, 0}, new double[] {c, 0}, new double[] {x, y});
  }

  private void drawTriangleAAS(IAST ast, GraphicState state, ContainerTag<?> parent) {
    double alpha = getDouble(ast.arg1());
    double beta = getDouble(ast.arg2());
    double a = getDouble(ast.arg3());
    double gamma = Math.PI - alpha - beta;
    if (gamma <= 0)
      return;
    double c = a * Math.sin(gamma) / Math.sin(alpha);
    double tanA = Math.tan(alpha);
    double tanB = Math.tan(beta);
    double x = c * tanB / (tanA + tanB);
    double y = x * tanA;
    drawPolygonPoints(state, parent, new double[] {0, 0}, new double[] {c, 0}, new double[] {x, y});
  }

  private void drawAxesWithTicks(ContainerTag<?> parent) {
    boolean showX = false;
    boolean showY = false;
    if (options.axes.isTrue()) {
      showX = true;
      showY = true;
    } else if (options.axes.isList2()) {
      showX = ((IAST) options.axes).arg1().isTrue();
      showY = ((IAST) options.axes).arg2().isTrue();
    }

    // Determine Axis positions.
    // Default to Automatic behavior (edges) if Origin is null.
    // If Log scale, Automatic means Bottom/Left edges of the view, NOT 0.
    double screenOx, screenOy;
    double oxMap, oyMap;

    if (options.axesOrigin != null) {
      double ox = options.axesOrigin[0];
      double oy = options.axesOrigin[1];

      if (isLog(options.scalingX) && ox <= 0)
        ox = (dataMinX > 0) ? dataMinX : LOG_MIN_CLAMP;
      if (isLog(options.scalingY) && oy <= 0)
        oy = (dataMinY > 0) ? dataMinY : LOG_MIN_CLAMP;

      oxMap = GraphicsOptions.getScalingFunction(options.scalingX).applyAsDouble(ox);
      oyMap = GraphicsOptions.getScalingFunction(options.scalingY).applyAsDouble(oy);
    } else {
      // Automatic Origin
      // For Log scales, default to the minimum visible value (edge of plot).
      // For Linear scales, default to 0 if 0 is sensible, else edge.
      if (isLog(options.scalingX))
        oxMap = mapMinX;
      else
        oxMap = 0.0;

      if (isLog(options.scalingY))
        oyMap = mapMinY;
      else
        oyMap = 0.0;
    }

    // Clamp axis drawing to the viewport
    if (oxMap < mapMinX)
      oxMap = mapMinX;
    if (oxMap > mapMaxX)
      oxMap = mapMaxX;
    if (oyMap < mapMinY)
      oyMap = mapMinY;
    if (oyMap > mapMaxY)
      oyMap = mapMaxY;

    screenOx = paddingLeft + (oxMap - mapMinX) * scaleX;
    screenOy = (options.imageSize[1] - paddingBottom) - (oyMap - mapMinY) * scaleY;

    double plotX1 = paddingLeft;
    double plotX2 = paddingLeft + (mapMaxX - mapMinX) * scaleX;
    double plotY2 = options.imageSize[1] - paddingBottom;
    double plotY1 = plotY2 - (mapMaxY - mapMinY) * scaleY;

    // Apply clamping again to ensure we don't draw outside due to float errors
    if (screenOx < plotX1)
      screenOx = plotX1;
    if (screenOx > plotX2)
      screenOx = plotX2;
    if (screenOy < plotY1)
      screenOy = plotY1;
    if (screenOy > plotY2)
      screenOy = plotY2;

    String style = "stroke:" + colorToCss(options.axesStyle.strokeColor) + ";stroke-width:"
        + fmt(options.axesStyle.strokeWidth) + "px;stroke-opacity:"
        + fmt(options.axesStyle.opacity);
    String textStyle = "fill:" + colorToCss(options.axesStyle.strokeColor)
        + ";font-family:sans-serif;font-size:10px";

    if (showX) {
      parent.with(tag("line").attr("x1", fmt(plotX1)).attr("y1", fmt(screenOy))
          .attr("x2", fmt(plotX2)).attr("y2", fmt(screenOy)).attr("style", style));
      if (isLog(options.scalingX))
        drawLogTicksX(screenOy, style, textStyle, parent);
      else
        drawTicksX(screenOy, style, textStyle, parent);
    }
    if (showY) {
      parent.with(tag("line").attr("x1", fmt(screenOx)).attr("y1", fmt(plotY1))
          .attr("x2", fmt(screenOx)).attr("y2", fmt(plotY2)).attr("style", style));
      if (isLog(options.scalingY))
        drawLogTicksY(screenOx, style, textStyle, parent);
      else
        drawTicksY(screenOx, style, textStyle, parent);
    }
  }

  private void drawTicksX(double y, String style, String textStyle, ContainerTag<?> parent) {
    double[] ticks = calculateTicks(mapMinX, mapMaxX);
    for (double val : ticks) {
      if (val < mapMinX || val > mapMaxX)
        continue;
      double sx = paddingLeft + (val - mapMinX) * scaleX;
      parent.with(tag("line").attr("x1", fmt(sx)).attr("y1", fmt(y)).attr("x2", fmt(sx))
          .attr("y2", fmt(y + 4)).attr("style", style));
      if (Math.abs(val) < 1e-10)
        val = 0.0;
      parent.with(tag("text").attr("x", fmt(sx)).attr("y", fmt(y + 14))
          .attr("style", textStyle + ";text-anchor:middle").withText(fmt(val)));
    }
  }

  private void drawLogTicksX(double y, String style, String textStyle, ContainerTag<?> parent) {
    double rawMin = dataMinX;
    double rawMax = dataMaxX;
    if (options.plotRange != null && !Double.isNaN(options.plotRange[0][0]))
      rawMin = options.plotRange[0][0];
    if (options.plotRange != null && !Double.isNaN(options.plotRange[0][1]))
      rawMax = options.plotRange[0][1];
    List<Double> ticks = getNiceLogTicks(rawMin, rawMax);
    for (double val : ticks) {
      double sx = mapX(val);
      parent.with(tag("line").attr("x1", fmt(sx)).attr("y1", fmt(y)).attr("x2", fmt(sx))
          .attr("y2", fmt(y + 4)).attr("style", style));
      parent.with(tag("text").attr("x", fmt(sx)).attr("y", fmt(y + 14))
          .attr("style", textStyle + ";text-anchor:middle").with(rawHtml(getLogTickLabel(val))));
    }
  }

  private void drawTicksY(double x, String style, String textStyle, ContainerTag<?> parent) {
    double[] ticks = calculateTicks(mapMinY, mapMaxY);
    for (double val : ticks) {
      if (val < mapMinY || val > mapMaxY)
        continue;
      double sy = (options.imageSize[1] - paddingBottom) - (val - mapMinY) * scaleY;
      parent.with(tag("line").attr("x1", fmt(x - 4)).attr("y1", fmt(sy)).attr("x2", fmt(x))
          .attr("y2", fmt(sy)).attr("style", style));
      if (Math.abs(val) < 1e-10)
        val = 0.0;
      parent.with(tag("text").attr("x", fmt(x - 6)).attr("y", fmt(sy + 4))
          .attr("style", textStyle + ";text-anchor:end").withText(fmt(val)));
    }
  }

  private void drawLogTicksY(double x, String style, String textStyle, ContainerTag<?> parent) {
    double rawMin = dataMinY;
    double rawMax = dataMaxY;
    if (options.plotRange != null && !Double.isNaN(options.plotRange[1][0]))
      rawMin = options.plotRange[1][0];
    if (options.plotRange != null && !Double.isNaN(options.plotRange[1][1]))
      rawMax = options.plotRange[1][1];
    List<Double> ticks = getNiceLogTicks(rawMin, rawMax);
    for (double val : ticks) {
      double sy = mapY(val);
      parent.with(tag("line").attr("x1", fmt(x - 4)).attr("y1", fmt(sy)).attr("x2", fmt(x))
          .attr("y2", fmt(sy)).attr("style", style));
      parent.with(tag("text").attr("x", fmt(x - 6)).attr("y", fmt(sy + 4))
          .attr("style", textStyle + ";text-anchor:end").with(rawHtml(getLogTickLabel(val))));
    }
  }

  private void drawGridLines(ContainerTag<?> parent) {
    if (options.gridLines == null || options.gridLines.isFalse() || options.gridLines.isNone())
      return;
    GraphicState style = options.gridLinesStyle;
    if (options.gridLines.isBuiltInSymbol()
        && ((IBuiltInSymbol) options.gridLines).ordinal() == ID.Automatic) {
      drawAutomaticXGrid(style, parent);
      drawAutomaticYGrid(style, parent);
    }
  }

  private void drawAutomaticXGrid(GraphicState state, ContainerTag<?> parent) {
    String style = getGridCss(state);
    if (isLog(options.scalingX)) {
      List<Double> ticks = getNiceLogTicks(dataMinX, dataMaxX);
      for (double val : ticks)
        drawVertGridLine(val, style, parent);
    } else {
      double[] ticks = calculateTicks(mapMinX, mapMaxX);
      for (double val : ticks) {
        if (val >= mapMinX && val <= mapMaxX)
          drawVertGridLine(val, style, parent);
      }
    }
  }

  private void drawAutomaticYGrid(GraphicState state, ContainerTag<?> parent) {
    String style = getGridCss(state);
    if (isLog(options.scalingY)) {
      List<Double> ticks = getNiceLogTicks(dataMinY, dataMaxY);
      for (double val : ticks)
        drawHorizGridLine(val, style, parent);
    } else {
      double[] ticks = calculateTicks(mapMinY, mapMaxY);
      for (double val : ticks) {
        if (val >= mapMinY && val <= mapMaxY)
          drawHorizGridLine(val, style, parent);
      }
    }
  }

  private void drawVertGridLine(double val, String style, ContainerTag<?> parent) {
    double sx = mapX(val);
    if (Double.isFinite(sx) && sx >= paddingLeft - 1e-5
        && sx <= options.imageSize[0] - paddingRight + 1e-5) {
      parent.with(tag("line").attr("x1", fmt(sx)).attr("y1", fmt(paddingTop)).attr("x2", fmt(sx))
          .attr("y2", fmt(options.imageSize[1] - paddingBottom)).attr("style", style));
    }
  }

  private void drawHorizGridLine(double val, String style, ContainerTag<?> parent) {
    double sy = mapY(val);
    if (Double.isFinite(sy) && sy >= paddingTop - 1e-5
        && sy <= options.imageSize[1] - paddingBottom + 1e-5) {
      parent.with(tag("line").attr("x1", fmt(paddingLeft)).attr("y1", fmt(sy))
          .attr("x2", fmt(options.imageSize[0] - paddingRight)).attr("y2", fmt(sy))
          .attr("style", style));
    }
  }

  private void drawFrame(ContainerTag<?> parent) {
    double plotX1 = paddingLeft;
    double plotX2 = paddingLeft + (mapMaxX - mapMinX) * scaleX;
    double plotY2 = options.imageSize[1] - paddingBottom;
    double plotY1 = plotY2 - (mapMaxY - mapMinY) * scaleY;

    parent.with(tag("rect").attr("x", fmt(plotX1)).attr("y", fmt(plotY1))
        .attr("width", fmt(plotX2 - plotX1)).attr("height", fmt(plotY2 - plotY1))
        .attr("fill", "none").attr("stroke", colorToCss(options.frameStyle.strokeColor))
        .attr("stroke-width", fmt(options.frameStyle.strokeWidth))
        .attr("stroke-opacity", fmt(options.frameStyle.opacity)));

    String tickStyle = "stroke:" + colorToCss(options.frameStyle.strokeColor) + ";stroke-width:"
        + fmt(options.frameStyle.strokeWidth) + "px;stroke-opacity:"
        + fmt(options.frameStyle.opacity);
    String textStyle = "fill:" + colorToCss(options.frameStyle.strokeColor)
        + ";font-family:sans-serif;font-size:10px";

    IExpr ticks = options.frameTicks;
    IExpr leftT = S.Automatic, rightT = S.None, bottomT = S.Automatic, topT = S.None;

    if (ticks.isList() && ticks.isList2()) {
      IAST tList = (IAST) ticks;
      if (tList.arg1().isList2() && tList.arg2().isList2()) {
        leftT = ((IAST) tList.arg1()).arg1();
        rightT = ((IAST) tList.arg1()).arg2();
        bottomT = ((IAST) tList.arg2()).arg1();
        topT = ((IAST) tList.arg2()).arg2();
      }
    }

    if (!bottomT.isNone())
      drawExplicitOrAutoTicksX(bottomT, plotY2, tickStyle, textStyle, "middle", 14, 4, parent);
    if (!topT.isNone())
      drawExplicitOrAutoTicksX(topT, plotY1, tickStyle, textStyle, "middle", -10, -4, parent);
    if (!leftT.isNone())
      drawExplicitOrAutoTicksY(leftT, plotX1, tickStyle, textStyle, "end", -6, -4, parent);
    if (!rightT.isNone())
      drawExplicitOrAutoTicksY(rightT, plotX2, tickStyle, textStyle, "start", 6, 4, parent);
  }

  private void drawExplicitOrAutoTicksX(IExpr spec, double y, String lineStyle, String textStyle,
      String anchor, double textOff, double lineOff, ContainerTag<?> parent) {
    if (spec.isList()) {
      for (IExpr t : (IAST) spec) {
        if (t.isList() && ((IAST) t).size() >= 2) {
          double val = getDouble(((IAST) t).arg1(), Double.NaN);
          String label = ((IAST) t).arg2().toString().replace("\"", "");
          if (!Double.isNaN(val)) {
            double sx = mapX(val);
            if (Double.isFinite(sx)) {
              parent.with(tag("line").attr("x1", fmt(sx)).attr("y1", fmt(y)).attr("x2", fmt(sx))
                  .attr("y2", fmt(y + lineOff)).attr("style", lineStyle));
              parent.with(tag("text").attr("x", fmt(sx)).attr("y", fmt(y + textOff))
                  .attr("style", textStyle + ";text-anchor:" + anchor).withText(label));
            }
          }
        }
      }
    } else {
      if (isLog(options.scalingX))
        drawLogTicksX(y, lineStyle, textStyle, parent);
      else
        drawTicksX(y, lineStyle, textStyle, parent);
    }
  }

  private void drawExplicitOrAutoTicksY(IExpr spec, double x, String lineStyle, String textStyle,
      String anchor, double textOff, double lineOff, ContainerTag<?> parent) {
    if (spec.isList()) {
      for (IExpr t : (IAST) spec) {
        if (t.isList() && ((IAST) t).size() >= 2) {
          double val = getDouble(((IAST) t).arg1(), Double.NaN);
          String label = ((IAST) t).arg2().toString().replace("\"", "");
          if (!Double.isNaN(val)) {
            double sy = mapY(val);
            if (Double.isFinite(sy)) {
              parent.with(tag("line").attr("x1", fmt(x)).attr("y1", fmt(sy))
                  .attr("x2", fmt(x + lineOff)).attr("y2", fmt(sy)).attr("style", lineStyle));
              parent.with(tag("text").attr("x", fmt(x + textOff)).attr("y", fmt(sy + 4))
                  .attr("style", textStyle + ";text-anchor:" + anchor).withText(label));
            }
          }
        }
      }
    } else {
      if (isLog(options.scalingY))
        drawLogTicksY(x, lineStyle, textStyle, parent);
      else
        drawTicksY(x, lineStyle, textStyle, parent);
    }
  }

  private void drawLegends(ContainerTag<?> parent, String gradientId) {
    if (options.plotLegends.isList()) {
      drawListLegend((IAST) options.plotLegends, parent);
    } else if (isBarLegend(options.plotLegends)) {
      drawBarLegend((IAST) options.plotLegends, gradientId, parent);
    }
  }

  private void drawListLegend(IAST legends, ContainerTag<?> parent) {
    int count = legends.argSize();
    double xBase = options.imageSize[0] - LEGEND_WIDTH + 10;
    double yBase = paddingTop + 10;
    double lineHeight = 20;

    parent.with(tag("rect").attr("x", fmt(xBase - 5)).attr("y", fmt(yBase - 10))
        .attr("width", fmt(LEGEND_WIDTH - 10)).attr("height", fmt(count * lineHeight + 10))
        .attr("fill", "white").attr("stroke", "#ccc").attr("stroke-width", "1"));

    for (int i = 0; i < count; i++) {
      IExpr label = legends.get(i + 1);
      Color c = Color.BLACK;
      RGBColor rgb = GraphicsOptions.plotStyleColor(i, options.plotStyleRaw);
      c = new Color(rgb.getRed(), rgb.getGreen(), rgb.getBlue());
      double y = yBase + i * lineHeight;

      if (options.joined) {
        parent.with(
            tag("line").attr("x1", fmt(xBase)).attr("y1", fmt(y - 4)).attr("x2", fmt(xBase + 20))
                .attr("y2", fmt(y - 4)).attr("stroke", colorToCss(c)).attr("stroke-width", "2"));
      } else {
        parent.with(tag("circle").attr("cx", fmt(xBase + 10)).attr("cy", fmt(y - 4)).attr("r", "3")
            .attr("fill", colorToCss(c)));
      }
      parent.with(tag("text").attr("x", fmt(xBase + 25)).attr("y", fmt(y)).attr("fill", "black")
          .attr("font-family", "sans-serif").attr("font-size", "11")
          .withText(label.toString().replace("\"", "")));
    }
  }

  private void drawBarLegend(IAST barLegend, String gradientId, ContainerTag<?> parent) {
    double min = 0.0, max = 1.0;
    if (barLegend.argSize() >= 2 && barLegend.arg2().isList()) {
      IAST range = (IAST) barLegend.arg2();
      min = getDouble(range.arg1(), 0.0);
      max = getDouble(range.arg2(), 1.0);
    }
    double xBase = options.imageSize[0] - LEGEND_WIDTH + 20;
    double yTop = paddingTop + 10;
    double barWidth = 15;
    double barHeight = options.imageSize[1] - paddingTop - paddingBottom - 20;
    double yBottom = yTop + barHeight;
    double mid = (min + max) / 2.0;

    parent.with(tag("rect").attr("x", fmt(xBase)).attr("y", fmt(yTop)).attr("width", fmt(barWidth))
        .attr("height", fmt(barHeight)).attr("fill", "url(#" + gradientId + ")")
        .attr("stroke", "#999").attr("stroke-width", "1"));

    String style = "fill:black;font-family:sans-serif;font-size:11px;text-anchor:start";
    parent.with(tag("text").attr("x", fmt(xBase + barWidth + 5)).attr("y", fmt(yTop + 10))
        .attr("style", style).withText(fmt(max)));
    parent.with(tag("text").attr("x", fmt(xBase + barWidth + 5)).attr("y", fmt(yBottom))
        .attr("style", style).withText(fmt(min)));
    parent.with(tag("text").attr("x", fmt(xBase + barWidth + 5))
        .attr("y", fmt(yTop + barHeight / 2.0 + 4)).attr("style", style).withText(fmt(mid)));
  }

  private boolean isMultiSegment(IExpr coords, GraphicState state) {
    if (!coords.isList() || ((IAST) coords).size() <= 1)
      return false;
    IExpr first = ((IAST) coords).arg1();
    if (!first.isList())
      return false;

    if (state.complexVertices != null) {
      // In GraphicsComplex: {{1,2}, {3,4}} implies multi-segment
      return true;
    } else {
      // Standard: {{{x,y},{x,y}}...} implies multi-segment
      if (((IAST) first).size() > 0 && ((IAST) first).arg1().isList()) {
        return true;
      }
    }
    return false;
  }

  private String getGridCss(GraphicState state) {
    return String.format(Locale.US,
        "stroke:%s; stroke-width:%.2fpx; stroke-opacity:%.2f; stroke-dasharray:%s; stroke-linecap:%s; stroke-linejoin:%s;",
        colorToCss(state.strokeColor), state.strokeWidth, state.opacity, state.dashArray,
        state.lineCap, state.lineJoin);
  }

  // --- Parsing Helpers ---

  private void parseOptions(IAST ast) {
    options.plotRangeAutomatic = true; // Default to true

    // options.axes default initialized to S.False
    for (int i = 2; i <= ast.argSize(); i++) {
      if (!ast.get(i).isRuleAST())
        continue;
      IAST rule = (IAST) ast.get(i);
      IExpr key = rule.arg1();
      IExpr val = rule.arg2();

      if (key.isBuiltInSymbol()) {
        switch (((IBuiltInSymbol) key).ordinal()) {
          case ID.Axes:
            options.axes = val; // Store the IExpr value (True, False, or List)
            break;
          case ID.Frame:
            options.frame = val.isTrue() || val == S.All;
            break;
          case ID.AxesOrigin:
            options.axesOrigin = parsePointRaw(val);
            break;
          case ID.GridLines:
            options.gridLines = val;
            break;
          case ID.GridLinesStyle:
            parseStyle(val, options.gridLinesStyle);
            break;
          case ID.ImageSize:
            imageSizeSet = true;
            if (val.isList())
              options.imageSize =
                  new double[] {getDouble(((IAST) val).arg1()), getDouble(((IAST) val).arg2())};
            else if (val.isNumber()) {
              double s = getDouble(val);
              options.imageSize = new double[] {s, s};
            }
            break;
          case ID.PlotRangePadding:
            if (val.isNone() || val.isFalse()) {
              options.plotRangePadding = 0.0;
            } else if (val.isNumber()) {
              options.plotRangePadding = getDouble(val, 0.05);
            }
            break;
          case ID.PlotRange:
            if (val.isList2()) {
              options.plotRange = new double[2][2];
              if (val.first().isList2() && val.second().isList2()) {
                options.plotRange[0][0] = getDoubleOrNaN(((IAST) val.get(1)).get(1));
                options.plotRange[0][1] = getDoubleOrNaN(((IAST) val.get(1)).get(2));
                options.plotRange[1][0] = getDoubleOrNaN(((IAST) val.get(2)).get(1));
                options.plotRange[1][1] = getDoubleOrNaN(((IAST) val.get(2)).get(2));
              } else {
                options.plotRange[0][0] = Double.NaN;
                options.plotRange[0][1] = Double.NaN;
                options.plotRange[1][0] = getDoubleOrNaN(val.first());
                options.plotRange[1][1] = getDoubleOrNaN(val.second());
              }
              options.plotRangeAutomatic = false; // Explicit range
            } else if (val == S.All) {
              options.plotRange = null;
              options.plotRangeAutomatic = false;
            } else if (val == S.Automatic) {
              options.plotRange = null;
              options.plotRangeAutomatic = true;
            }
            break;
          case ID.AspectRatio:
            if (val == S.Automatic) {
              options.aspectRatio = Double.POSITIVE_INFINITY;
            } else {
              try {
                options.aspectRatio = val.evalf();
              } catch (Exception e) {
                options.aspectRatio = Double.POSITIVE_INFINITY;
              }
            }
            break;
          case ID.AxesStyle:
            parseStyle(val, options.axesStyle);
            break;
          case ID.FrameStyle:
            parseStyle(val, options.frameStyle);
            break;
          case ID.PlotStyle:
          case ID.BaseStyle:
            parseStyle(val, options.globalStyle);
            options.plotStyleRaw = val;
            break;
          case ID.PlotLegends:
            options.plotLegends = val;
            break;
          case ID.Joined:
            options.joined = val.isTrue();
            break;
          case ID.$Scaling:
            if (val.isList()) {
              if (val.size() > 1)
                options.scalingX = val.getAt(1).toString().replace("\"", "");
              if (val.size() > 2)
                options.scalingY = val.getAt(2).toString().replace("\"", "");
            } else if (val.isString())
              options.scalingY = val.toString().replace("\"", "");
            break;
          case ID.FrameTicks:
            options.frameTicks = val;
            break;
          case ID.Background:
            if (!val.isNone()) {
              options.background = toColor(val);
            }
            break;
          case ID.Epilog:
            options.epilog = val;
            break;
          case ID.Prolog:
            options.prolog = val;
            break;
        }
      }
    }
  }

  private Color toColor(IExpr expr) {
    if (expr.isAST()) {
      return parseColor(expr);
    }
    if (expr.isBuiltInSymbol()) {
      switch (((IBuiltInSymbol) expr).ordinal()) {
        case ID.Red:
          return Color.RED;
        case ID.Green:
          return Color.GREEN;
        case ID.Blue:
          return Color.BLUE;
        case ID.Black:
          return Color.BLACK;
        case ID.White:
          return Color.WHITE;
        case ID.Gray:
          return Color.GRAY;
        case ID.Yellow:
          return Color.YELLOW;
        case ID.Cyan:
          return Color.CYAN;
        case ID.Magenta:
          return Color.MAGENTA;
        case ID.Orange:
          return Color.ORANGE;
        case ID.Pink:
          return Color.PINK;
        case ID.Purple:
          return new Color(128, 0, 128);
        case ID.Brown:
          return new Color(165, 42, 42);
        case ID.LightGray:
          return new Color(211, 211, 211);
        case ID.DarkGray:
          return new Color(64, 64, 64);
        case ID.Transparent:
          return new Color(0, 0, 0, 0);
      }
    }
    return null;
  }

  private void parseStyle(IExpr styleExpr, GraphicState state) {
    if (styleExpr.isList()) {
      IAST list = (IAST) styleExpr;
      for (int i = 1; i <= list.argSize(); i++)
        parseStyleElement(list.get(i), state);
    } else {
      parseStyleElement(styleExpr, state);
    }
  }

  private void parseStyleElement(IExpr expr, GraphicState state) {
    if (expr.isBuiltInSymbol())
      processSymbol((IBuiltInSymbol) expr, state);
    else if (expr.isAST()) {
      IAST ast = (IAST) expr;
      IExpr head = ast.head();
      if (head.isBuiltInSymbol()) {
        switch (((IBuiltInSymbol) head).ordinal()) {
          case ID.RGBColor:
          case ID.Hue:
          case ID.GrayLevel:
          case ID.CMYKColor:
            state.strokeColor = parseColor(ast);
            break;
          case ID.Thickness:
            state.strokeWidth = getDouble(ast.arg1()) * options.imageSize[0];
            break;
          case ID.AbsoluteThickness:
            state.strokeWidth = getDouble(ast.arg1(), 1.0);
            break;
          case ID.Opacity:
            state.opacity = getDouble(ast.arg1());
            break;
          case ID.PointSize:
            updatePointSize(ast, state);
            break;
          case ID.AbsolutePointSize:
            state.pointRadius = getDouble(ast.arg1(), 3.0);
            break;
          case ID.Rule:
            processRule(ast, state);
            break;
          case ID.Directive:
            for (int i = 1; i <= ast.argSize(); i++) {
              parseStyle(ast.get(i), state);
            }
            break;
        }
      }
    }
  }

  private void updateBounds(double x, double y) {
    if (isLog(options.scalingX) && x <= 0)
      return;
    if (isLog(options.scalingY) && y <= 0)
      return;
    if (x < dataMinX)
      dataMinX = x;
    if (x > dataMaxX)
      dataMaxX = x;
    if (y < dataMinY)
      dataMinY = y;
    if (y > dataMaxY)
      dataMaxY = y;
    if (Double.isFinite(y))
      allYValues.add(y);
  }


  private void scanBounds(IExpr expr, GraphicState state) {
    if (expr.isList()) {
      for (IExpr e : (IAST) expr)
        scanBounds(e, state);
      return;
    }

    if (!expr.isAST())
      return;

    IAST ast = (IAST) expr;
    IExpr head = ast.head();
    if (head.isBuiltInSymbol()) {
      switch (((IBuiltInSymbol) head).ordinal()) {
        case ID.Point:
        case ID.Line:
        case ID.Arrow:
        case ID.Polygon:
        case ID.BezierCurve:
        case ID.BSplineCurve:
        case ID.Triangle:
          List<double[]> pts = extractPoints(ast.arg1(), state);
          for (double[] p : pts) {
            updateBounds(p[0], p[1]);
          }
          break;
        case ID.Rectangle:
          double[] r1 = parsePoint(ast.arg1(), state);
          double[] r2;
          if (ast.argSize() > 1)
            r2 = parsePoint(ast.arg2(), state);
          else
            r2 = new double[] {r1[0] + 1.0, r1[1] + 1.0};
          updateBounds(r1[0], r1[1]);
          updateBounds(r2[0], r2[1]);
          break;
        case ID.Circle:
        case ID.Disk:
          double[] c = parsePoint(ast.arg1(), state);
          double r = getDouble(ast, 2, 1.0);
          updateBounds(c[0] - r, c[1] - r);
          updateBounds(c[0] + r, c[1] + r);
          break;
        case ID.GraphicsComplex:
          // Arg1 is the list of points
          List<double[]> gcPts = extractPoints(ast.arg1(), state);
          for (double[] p : gcPts) {
            updateBounds(p[0], p[1]);
          }
          // Arg2 (if present) contains primitives referring to these points.
          if (ast.argSize() >= 2) {
            GraphicState complexState = state.clone();
            complexState.complexVertices = gcPts;
            scanBounds(ast.arg2(), complexState);
          }
          break;
        case ID.Text:
        case ID.Inset:
          if (ast.argSize() >= 2) {
            double[] pos = parsePoint(ast.arg2(), state);
            updateBounds(pos[0], pos[1]);
          }
          break;
        case ID.GraphicsGroup:
        case ID.Style:
          // Recurse on the first argument (the primitive or list of primitives)
          scanBounds(ast.arg1(), state);
          break;
      }
    }
  }

  private double[] parsePointRaw(IExpr expr) {
    if (expr.isList())
      return new double[] {getDouble(((IAST) expr).arg1()), getDouble(((IAST) expr).arg2())};
    return new double[] {0, 0};
  }

  private double[] parsePoint(IExpr expr, GraphicState state) {
    if (state.complexVertices != null && expr.isInteger()) {
      int idx = expr.toIntDefault(0);
      if (idx > 0 && idx <= state.complexVertices.size()) {
        return state.complexVertices.get(idx - 1);
      }
    }
    return parsePointRaw(expr);
  }

  /**
   * Corrected extractPoints to handle nested index lists in GraphicsComplex recursively.
   */
  private List<double[]> extractPoints(IExpr expr, GraphicState state) {
    List<double[]> pts = new ArrayList<>();
    if (expr.isList()) {
      IAST list = (IAST) expr;

      // Determine if we should treat this list as a coordinate {x, y} directly.
      // Outside GraphicsComplex, {x, y} (numbers) is a coordinate.
      // Inside GraphicsComplex, we typically see indices (Integers) or Lists of Indices.
      boolean treatAsCoordinate = list.argSize() > 0 && list.arg1().isNumber();
      if (state.complexVertices != null) {
        treatAsCoordinate = false;
      }

      if (treatAsCoordinate) {
        pts.add(parsePoint(list, state));
      } else {
        for (int i = 1; i <= list.argSize(); i++) {
          IExpr elem = list.get(i);
          // If we are in a GraphicsComplex and the element is a list (e.g. {i1, i2, i3, ...}),
          // we must recurse to extract vertices for all those indices.
          if (state.complexVertices != null && elem.isList()) {
            pts.addAll(extractPoints(elem, state));
          } else {
            pts.add(parsePoint(elem, state));
          }
        }
      }
    } else if (state.complexVertices != null && expr.isInteger()) {
      pts.add(parsePoint(expr, state));
    }
    return pts;
  }

  private double getDouble(IExpr expr, double def) {
    try {
      if (expr instanceof INumber)
        return ((INumber) expr).reDoubleValue();
      return expr.evalf();
    } catch (RuntimeException e) {
    }
    return def;
  }

  private double getDouble(IExpr expr) {
    return getDouble(expr, 0.0);
  }

  private double getDoubleOrNaN(IExpr expr) {
    return getDouble(expr, Double.NaN);
  }

  private double getDouble(IAST ast, int position, double defaultValue) {
    try {
      if (ast.argSize() < position)
        return defaultValue;
      IExpr expr = ast.get(position);
      return getDouble(expr, defaultValue);
    } catch (RuntimeException e) {
      return defaultValue;
    }
  }

  private Color parseColor(IExpr expr) {
    if (!expr.isAST())
      return Color.BLACK;
    IAST ast = (IAST) expr;
    IExpr head = ast.head();
    if (head.isBuiltInSymbol()) {
      switch (((IBuiltInSymbol) head).ordinal()) {
        case ID.RGBColor:
          float ra = (ast.argSize() >= 4) ? (float) getDouble(ast.arg4()) : 1.0f;
          return new Color(clamp(getDouble(ast.arg1())), clamp(getDouble(ast.arg2())),
              clamp(getDouble(ast.arg3())), ra);
        case ID.Hue:
          float h = (float) getDouble(ast.arg1());
          float s = (ast.argSize() >= 2) ? (float) getDouble(ast.arg2()) : 1.0f;
          float b = (ast.argSize() >= 3) ? (float) getDouble(ast.arg3()) : 1.0f;
          float ha = (ast.argSize() >= 4) ? (float) getDouble(ast.arg4()) : 1.0f;
          Color c = Color.getHSBColor(h, s, b);
          return new Color(c.getRed(), c.getGreen(), c.getBlue(), (int) (ha * 255));
        case ID.GrayLevel:
          float g = (float) getDouble(ast.arg1());
          float ga = (ast.argSize() >= 2) ? (float) getDouble(ast.arg2()) : 1.0f;
          return new Color(g, g, g, ga);
        case ID.CMYKColor:
          float c1 = (float) getDouble(ast.arg1());
          float m = (float) getDouble(ast.arg2());
          float y = (float) getDouble(ast.arg3());
          float k = (float) getDouble(ast.arg4());
          float ca = (ast.argSize() >= 5) ? (float) getDouble(ast.get(5)) : 1.0f;
          return new Color(clamp((1 - c1) * (1 - k)), clamp((1 - m) * (1 - k)),
              clamp((1 - y) * (1 - k)), ca);
        case ID.Lighter:
          return getLighterDarkerColor(ast, true);
        case ID.Darker:
          return getLighterDarkerColor(ast, false);
      }
    }
    return Color.BLACK;
  }

  private Color getLighterDarkerColor(IAST ast, boolean isLighter) {
    if (ast.size() < 2)
      return Color.BLACK;
    Color base = toColor(ast.arg1());
    if (base == null)
      base = Color.BLACK;
    double fraction = 1.0 / 3.0;
    if (ast.size() >= 3) {
      fraction = getDouble(ast.arg2());
    }
    if (fraction < 0)
      fraction = 0;
    if (fraction > 1)
      fraction = 1;

    float r = base.getRed() / 255.0f;
    float g = base.getGreen() / 255.0f;
    float b = base.getBlue() / 255.0f;
    float a = base.getAlpha() / 255.0f;

    float newR, newG, newB;
    if (isLighter) {
      newR = (float) (r * (1.0 - fraction) + fraction);
      newG = (float) (g * (1.0 - fraction) + fraction);
      newB = (float) (b * (1.0 - fraction) + fraction);
    } else {
      newR = (float) (r * (1.0 - fraction));
      newG = (float) (g * (1.0 - fraction));
      newB = (float) (b * (1.0 - fraction));
    }
    return new Color(clamp(newR), clamp(newG), clamp(newB), a);
  }

  private float clamp(double val) {
    return (float) Math.max(0.0, Math.min(1.0, val));
  }

  private String colorToCss(Color c) {
    return (c.getAlpha() == 0 && c.getRed() == 0 && c.getGreen() == 0 && c.getBlue() == 0) ? "none"
        : String.format(Locale.US, "rgb(%d,%d,%d)", c.getRed(), c.getGreen(), c.getBlue());
  }


}

