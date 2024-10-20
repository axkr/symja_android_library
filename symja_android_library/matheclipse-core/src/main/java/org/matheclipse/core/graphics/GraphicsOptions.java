package org.matheclipse.core.graphics;

import java.util.Locale;
import java.util.function.Function;
import org.matheclipse.core.builtin.GraphicsFunctions;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.convert.RGBColor;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Options for the 2D Graphics object.
 *
 */
public class GraphicsOptions {
  public static IAST BLACK = F.RGBColor(F.C0, F.C0, F.C0);

  public static double TINY_POINTSIZE = 0.00032;
  public static double SMALL_POINTSIZE = 0.00065;
  public static double MEDIUM_POINTSIZE = 0.0013;
  public static double LARGE_POINTSIZE = 0.0026;

  public static int TINY_FONTSIZE = 4;
  public static int SMALL_FONTSIZE = 8;
  public static int MEDIUM_FONTSIZE = 12;
  public static int LARGE_FONTSIZE = 24;

  public static double TINY_THICKNESS = 0.005;
  public static double SMALL_THICKNESS = 0.01;
  public static double MEDIUM_THICKNESS = 0.02;
  public static double LARGE_THICKNESS = 0.03;


  /** Default plot style colors for functions */
  public static final RGBColor[] PLOT_COLORS = new RGBColor[] { //
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

  protected static void addPadding(double[] boundingbox) {
    // add some "padding" around bounding box
    double xPadding = (boundingbox[1] - boundingbox[0]) / 12.0;
    double yPadding = (boundingbox[3] - boundingbox[2]) / 12.0;
    if (F.isZero(xPadding)) {
      xPadding = 0.05;
    }
    if (F.isZero(yPadding)) {
      yPadding = 0.05;
    }
    boundingbox[0] = boundingbox[0] - xPadding; // xMin
    boundingbox[1] = boundingbox[1] + xPadding; // xMax
    boundingbox[2] = boundingbox[2] - yPadding; // yMin
    boundingbox[3] = boundingbox[3] + yPadding; // yMax
  }

  public static Function<IExpr, IExpr> getScaling(ArrayNode array, IExpr scale) {
    if (scale.isString()) {
      String scaleStr = scale.toString();
      String lowerScaleStr = scale.toString().toLowerCase(Locale.US);
      if (scaleStr.equals("Log")) {
        array.add(lowerScaleStr);
        return x -> F.Log(x);
      } else if (scaleStr.equals("Log2")) {
        array.add(lowerScaleStr);
        return x -> F.Log(x, F.C2);
      } else if (scaleStr.equals("Log10")) {
        array.add(lowerScaleStr);
        return x -> F.Log(x, F.C10);
      }
    }
    array.add("none");
    // Identity
    return x -> x;
  }

  public static Function<IExpr, IExpr> getScaling(IExpr scale) {
    if (scale.isString()) {
      String scaleStr = scale.toString();
      if (scaleStr.equals("Log")) {
        return x -> F.Log(x);
      } else if (scaleStr.equals("Log2")) {
        return x -> F.Log(x, F.C2);
      } else if (scaleStr.equals("Log10")) {
        return x -> F.Log(x, F.C10);
      }
    }
    // Identity
    return x -> x;
  }

  public static void optionBoolean(ArrayNode arrayNode, String optionName, boolean value) {
    ObjectNode jsonDefaults = GraphicsFunctions.JSON_OBJECT_MAPPER.createObjectNode();
    jsonDefaults.put("option", optionName);
    jsonDefaults.put("value", value);
    arrayNode.add(jsonDefaults);
  }

  public static void optionDouble(ArrayNode arrayNode, String optionName, double value) {
    ObjectNode jsonDefaults = arrayNode.objectNode();
    optionDouble(jsonDefaults, optionName, value);
    arrayNode.add(jsonDefaults);
  }

  private static void optionDouble(ObjectNode objectNode, String optionName, double value) {
    objectNode.put("option", optionName);
    objectNode.put("value", value);
  }

  public static void optionInt(ArrayNode arrayNode, String optionName, int value) {
    ObjectNode jsonDefaults = GraphicsFunctions.JSON_OBJECT_MAPPER.createObjectNode();
    jsonDefaults.put("option", optionName);
    jsonDefaults.put("value", value);
    arrayNode.add(jsonDefaults);
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
    return PLOT_COLORS[(functionColorNumber) % PLOT_COLORS.length];
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
    RGBColor color = GraphicsOptions.plotStyleColor(functionColorNumber, plotStyle);
    float[] rgbComponents = color.getRGBColorComponents(null);
    return F.RGBColor(rgbComponents[0], rgbComponents[1], rgbComponents[2]);
  }

  private static void setColor(ObjectNode json, double red, double green, double blue) {
    ArrayNode arrayNode = json.arrayNode();
    arrayNode.add(red);
    arrayNode.add(green);
    arrayNode.add(blue);
    json.set("color", arrayNode);
  }

  public static void setColor(ObjectNode json, IAST color, IAST defaultColor, boolean color3D) {
    if (color.isPresent()) {
      if (color.isAST(S.RGBColor, 4, 5)) {
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
        // if (color.size() == 5) {
        // double opacity = list.arg4().toDoubleDefault(1.0);
        // json.put("opacity", opacity);
        // }
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
    if (defaultColor.isAST(S.RGBColor, 4, 5)) {
      double red = defaultColor.arg1().toDoubleDefault(0.0);
      double green = defaultColor.arg2().toDoubleDefault(0.0);
      double blue = defaultColor.arg3().toDoubleDefault(0.0);
      ArrayNode arrayNode = json.arrayNode();
      arrayNode.add(red);
      arrayNode.add(green);
      arrayNode.add(blue);
      json.set("color", arrayNode);
    } else {
      // black
      ArrayNode arrayNode = json.arrayNode();
      if (color3D) {
        arrayNode.add(1.0);
        arrayNode.add(0.5);
        arrayNode.add(0.0);
      } else {
        arrayNode.add(0.0);
        arrayNode.add(0.0);
        arrayNode.add(0.0);
      }
      json.set("color", arrayNode);
    }

  }

  public static void setColorOption(ObjectNode json, double red, double green, double blue) {
    ArrayNode arrayNode = json.arrayNode();
    arrayNode.add(red);
    arrayNode.add(green);
    arrayNode.add(blue);
    json.put("option", "color");
    json.set("value", arrayNode);
  }

  public static void setColorOption(ArrayNode arrayNode, IAST color) {
    if (color.isPresent()) {
      if (color.isAST(S.RGBColor, 4, 5)) {
        double red = color.arg1().toDoubleDefault(0.0);
        double green = color.arg2().toDoubleDefault(0.0);
        double blue = color.arg3().toDoubleDefault(0.0);
        ObjectNode g = arrayNode.objectNode();
        setColorOption(g, red, green, blue);
        arrayNode.add(g);
        double opacity = 1.0;
        if (color.argSize() == 4) {
          opacity = color.arg4().toDoubleDefault(1.0);
        }
        GraphicsOptions.optionDouble(arrayNode, "opacity", opacity);
        return;
      } else if (color.isAST(S.RGBColor, 1) && color.arg1().isAST(S.List, 4, 5)) {
        IAST list = (IAST) color.arg1();
        double red = list.arg1().toDoubleDefault(0.0);
        double green = list.arg2().toDoubleDefault(0.0);
        double blue = list.arg3().toDoubleDefault(0.0);
        ObjectNode g = arrayNode.objectNode();
        setColorOption(g, red, green, blue);
        arrayNode.add(g);
        double opacity = 1.0;
        if (list.argSize() == 4) {
          opacity = list.arg4().toDoubleDefault(1.0);
        }

        GraphicsOptions.optionDouble(arrayNode, "opacity", opacity);
        return;
      }
    }
    // black
    ObjectNode g = arrayNode.objectNode();
    setColorOption(g, 0.0, 0.0, 0.0);
    arrayNode.add(g);
  }

  public static boolean setGrayLevel(ObjectNode g, IAST grayLevel) {
    RGBColor rgb = null;
    if (grayLevel.isAST1() || grayLevel.isAST2()) {
      rgb = RGBColor.getGrayLevel((float) grayLevel.arg1().evalf());
    }
    if (rgb != null) {
      setColorOption(g, rgb.getRed() / 255.0, rgb.getGreen() / 255.0, rgb.getBlue() / 255.0);
      return true;
    }
    return false;
  }

  public boolean setHueColor(ArrayNode arrayNode, IAST hueColor) {
    RGBColor rgb = RGBColor.hueToRGB(hueColor);
    if (hueColor.argSize() == 4) {
      opacity = hueColor.arg4().toDoubleDefault(1.0);
    }
    if (rgb != null) {
      ObjectNode g = arrayNode.objectNode();
      setColorOption(g, rgb.getRed() / 255.0, rgb.getGreen() / 255.0, rgb.getBlue() / 255.0);
      arrayNode.add(g);
      GraphicsOptions.optionDouble(arrayNode, "opacity", opacity);
      return true;
    }
    return false;
  }

  double opacity = 1.0;

  double pointSize = GraphicsOptions.MEDIUM_POINTSIZE;

  double thickness = GraphicsOptions.MEDIUM_THICKNESS;

  int fontSize = GraphicsOptions.MEDIUM_FONTSIZE;

  IAST rgbColor;

  int colorIndex = 0;

  /**
   * If <code>true</code> points in a dataset should be joined into a line, otherwise they should be
   * plotted as separate points.
   */
  boolean joined = false;

  IExpr axes = S.False;

  IExpr axesLabel = S.None;

  IExpr plotLabel = S.None;

  IExpr plotLegends = S.None;

  double[] boundingbox =
      new double[] {Double.MAX_VALUE, Double.MIN_VALUE, Double.MAX_VALUE, Double.MIN_VALUE};

  String xScale = "";

  String yScale = "";

  Function<IExpr, IExpr> xFunction;

  Function<IExpr, IExpr> yFunction;

  OptionArgs options;

  public GraphicsOptions(EvalEngine engine) {
    // Identity mapping as default
    xFunction = x -> x;
    yFunction = y -> y;
    rgbColor = GraphicsOptions.BLACK;

    options = new OptionArgs(engine);
    thickness = TINY_THICKNESS;
  }

  public void addPadding() {
    addPadding(this.boundingbox);
  }

  /**
   * Add the data for the points. Depending on {@link GraphicsOptions#isJoined()} create a
   * {@link F#Line(IExpr)} or {@link F#Point(IExpr)} expression.
   * 
   * @param pointPrimitives
   * @return
   */
  public IAST addPoints(IAST pointPrimitives) {
    return joined ? F.Line(pointPrimitives) : F.Point(pointPrimitives);
  }

  public double[] boundingBox() {
    return boundingbox;
  }

  public GraphicsOptions copy() {
    GraphicsOptions graphicsOptions = new GraphicsOptions(EvalEngine.get());
    graphicsOptions.boundingbox = new double[] {//
        this.boundingbox[0], this.boundingbox[1], this.boundingbox[2], this.boundingbox[3]};
    graphicsOptions.fontSize = this.fontSize;
    graphicsOptions.joined = this.joined;
    graphicsOptions.opacity = this.opacity;
    graphicsOptions.options = this.options;
    graphicsOptions.thickness = this.thickness;
    graphicsOptions.rgbColor = this.rgbColor;
    graphicsOptions.xFunction = this.xFunction;
    graphicsOptions.yFunction = this.yFunction;
    return graphicsOptions;
  }

  public int fontSize() {
    return fontSize;
  }

  public int getColorIndex() {
    return colorIndex;
  }

  public boolean graphics2DAxes(ObjectNode axes) {
    OptionArgs options = options();

    ArrayNode scalingArray = null;
    IExpr scalingFunctions = options.getOption(S.$Scaling);
    if (scalingFunctions.isPresent()) {
      if (scalingFunctions.isList1()) {
        scalingArray = GraphicsFunctions.JSON_OBJECT_MAPPER.createArrayNode();
        setXFunction(GraphicsOptions.getScaling(scalingArray, scalingFunctions.first()));
        scalingArray.add("none");
        setYFunction(y -> y);
      } else if (scalingFunctions.isList2()) {
        scalingArray = GraphicsFunctions.JSON_OBJECT_MAPPER.createArrayNode();
        setXFunction(GraphicsOptions.getScaling(scalingArray, scalingFunctions.first()));
        setYFunction(GraphicsOptions.getScaling(scalingArray, scalingFunctions.second()));
      } else if (!scalingFunctions.isList()) {
        scalingArray = GraphicsFunctions.JSON_OBJECT_MAPPER.createArrayNode();
        scalingArray.add("none");
        setXFunction(x -> x);
        setYFunction(GraphicsOptions.getScaling(scalingArray, scalingFunctions));
      } else {
        return false;
      }
    }

    ObjectNode g = GraphicsFunctions.JSON_OBJECT_MAPPER.createObjectNode();
    IExpr axesOptions = options.getOption(S.Axes);
    if (!axesOptions.isPresent()) {
      axesOptions = S.False;
    }
    if (axesOptions.isList2()) {
      IExpr a1 = axesOptions.first();
      IExpr a2 = axesOptions.second();
      hasAxesJSON(g, a1, a2);
    } else if (axesOptions.isTrue()) {
      hasAxesJSON(g, S.True, S.True);
    } else if (axesOptions.isFalse()) {
      hasAxesJSON(g, S.False, S.False);
    } else {
      return false;
    }

    if (scalingArray != null) {
      g.set("scaling", scalingArray);
    }
    axes.set("axes", g);
    return true;
  }

  private static void hasAxesJSON(ObjectNode g, IExpr a1, IExpr a2) {
    ArrayNode an = GraphicsFunctions.JSON_OBJECT_MAPPER.createArrayNode();
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
  }

  public void graphics2DFilling(ArrayNode arrayNode) {
    OptionArgs options = options();
    IExpr filling = options.getOption(S.Filling);
    if (filling.isPresent()) {
      ObjectNode g = GraphicsFunctions.JSON_OBJECT_MAPPER.createObjectNode();
      g.put("option", "filling");
      if (filling == S.None) {
        g.put("value", "none");
      } else if (filling == S.Axis) {
        g.put("value", "axis");
      } else if (filling == S.Top) {
        g.put("value", "top");
      } else if (filling == S.Bottom) {
        g.put("value", "bottom");
      } else {
        return;
      }
      arrayNode.add(g);
    }
  }

  public void graphics2DScalingFunctions(ArrayNode arrayNode) {
    OptionArgs options = options();
    IExpr scalingFunctions = options.getOption(S.$Scaling);
    if (scalingFunctions.isPresent()) {
      ObjectNode g = GraphicsFunctions.JSON_OBJECT_MAPPER.createObjectNode();
      ArrayNode array = GraphicsFunctions.JSON_OBJECT_MAPPER.createArrayNode();
      if (scalingFunctions.isList1()) {
        setXFunction(GraphicsOptions.getScaling(array, scalingFunctions.first()));
        array.add("none");
        setYFunction(y -> y);
      } else if (scalingFunctions.isList2()) {
        setXFunction(GraphicsOptions.getScaling(array, scalingFunctions.first()));
        setYFunction(GraphicsOptions.getScaling(array, scalingFunctions.second()));
      } else if (!scalingFunctions.isList()) {
        array.add("none");
        setXFunction(x -> x);
        setYFunction(GraphicsOptions.getScaling(array, scalingFunctions));
      } else {
        return;
      }
      g.put("option", "scaling");
      g.set("value", array);
      arrayNode.add(g);
    }
  }

  public boolean graphicsExtent2D(ObjectNode objectNode, IAST plotRange) {
    if (plotRange.isList2()) {
      IExpr arg1 = plotRange.first();
      IExpr arg2 = plotRange.second();
      if (arg1.isList2() && arg2.isList2()) {
        boundingbox[0] = arg1.first().evalf();
        boundingbox[1] = arg1.second().evalf();
        boundingbox[2] = arg2.first().evalf();
        boundingbox[3] = arg2.second().evalf();
        objectNode.put("xmin", boundingbox[0]);
        objectNode.put("xmax", boundingbox[1]);
        objectNode.put("ymin", boundingbox[2]);
        objectNode.put("ymax", boundingbox[3]);
        return true;
      } else {
        objectNode.put("xmin", boundingbox[0]);
        objectNode.put("xmax", boundingbox[1]);
        boundingbox[2] = arg1.evalf();
        boundingbox[3] = arg2.evalf();
        objectNode.put("ymin", boundingbox[2]);
        objectNode.put("ymax", boundingbox[3]);
        return true;
      }
    }
    return false;
  }

  public boolean graphicsExtent2D(ObjectNode objectNode) {
    objectNode.put("xmin", boundingbox[0]);
    objectNode.put("xmax", boundingbox[1]);
    objectNode.put("ymin", boundingbox[2]);
    objectNode.put("ymax", boundingbox[3]);
    return true;
  }

  public int incColorIndex() {
    if (PLOT_COLORS.length - 1 == colorIndex) {
      colorIndex = 1;
      return PLOT_COLORS.length - 1;
    }
    return colorIndex++;
  }

  /**
   * If <code>true</code> points in a dataset should be joined into a line, otherwise they should be
   * plotted as separate points.
   * 
   * @return
   */
  public boolean isJoined() {
    return joined;
  }

  public void mergeOptions(IAST listOfOptions, double[] yMinMax) {
    for (int i = 1; i < listOfOptions.size(); i++) {
      if (listOfOptions.get(i).isRuleAST()) {
        IExpr option = listOfOptions.get(i).first();
        if (option == S.PlotRange) {
          IExpr plotRange = listOfOptions.get(i).second();
          if (plotRange.isList2()) {
            try {
              IExpr arg1 = plotRange.first();
              IExpr arg2 = plotRange.second();
              if (arg1.isList2() && arg2.isList2()) {
                boundingbox[0] = arg1.first().evalf();
                boundingbox[1] = arg1.second().evalf();
                boundingbox[2] = arg2.first().evalf();
                boundingbox[3] = arg2.second().evalf();
                yMinMax[0] = boundingbox[2];
                yMinMax[1] = boundingbox[3];
              } else {
                boundingbox[2] = arg1.evalf();
                boundingbox[3] = arg2.evalf();
                yMinMax[0] = boundingbox[2];
                yMinMax[1] = boundingbox[3];
              }
            } catch (ArgumentTypeException ate) {
              // ignore false plot ranges
            }
          }
        }
      }
    }
  }

  public double opacity() {
    return opacity;
  }

  public OptionArgs options() {
    return options;
  }

  public IExpr axes() {
    return axes;
  }

  public IExpr axesLabel() {
    return axesLabel;
  }

  public IExpr plotLabel() {
    return plotLabel;
  }

  public IExpr plotLegends() {
    return plotLegends;
  }

  /**
   * Return <code>PlotRange</code> (extent) option rule.
   * 
   * @param boundingbox
   * @return
   */
  public IAST plotRange() {
    return F.Rule(S.PlotRange, F.List(F.List(F.num(boundingbox[0]), F.num(boundingbox[1])),
        F.List(F.num(boundingbox[2]), F.num(boundingbox[3]))));
  }

  public IAST point(double x, double y) {
    return F.List(F.num(x), F.num(y));
  }

  public double pointSize() {
    return pointSize;
  }

  public double pointSize(IAST pointSizeAST) {
    if (pointSizeAST.isAST(S.PointSize, 2)) {
      try {
        IExpr arg1 = pointSizeAST.arg1();
        if (arg1.isBuiltInSymbol()) {
          if (arg1 == S.Large) {
            pointSize = LARGE_POINTSIZE;
          } else if (arg1 == S.Medium) {
            pointSize = MEDIUM_POINTSIZE;
          } else if (arg1 == S.Small) {
            pointSize = SMALL_POINTSIZE;
          } else if (arg1 == S.Tiny) {
            pointSize = TINY_POINTSIZE;
          } else {
            pointSize = pointSizeAST.arg1().evalf();
          }
        } else {
          pointSize = pointSizeAST.arg1().evalf();
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        pointSize = MEDIUM_POINTSIZE;
      }
    }
    return pointSize;
  }

  public void setAxes(IExpr axes) {
    this.axes = axes;
  }

  public void setAxesLabel(IExpr axesLabel) {
    this.axesLabel = axesLabel;
  }

  public void setBoundingBox(double[] boundingbox) {
    this.boundingbox = boundingbox;
  }

  public void setBoundingBoxScaled(double[] boundingbox) {
    try {
      // first do all evaluations, so if exception is raised no value is changed
      double x0 = xFunction.apply(F.num(boundingbox[0])).evalf();
      double x1 = xFunction.apply(F.num(boundingbox[1])).evalf();
      double y0 = yFunction.apply(F.num(boundingbox[2])).evalf();
      double y1 = yFunction.apply(F.num(boundingbox[3])).evalf();
      if (x0 < this.boundingbox[0]) {
        this.boundingbox[0] = x0;
      }
      if (x1 > this.boundingbox[1]) {
        this.boundingbox[1] = x1;
      }
      if (y0 < this.boundingbox[2]) {
        this.boundingbox[2] = y0;
      }
      if (y1 > this.boundingbox[3]) {
        this.boundingbox[3] = y1;
      }
    } catch (ArgumentTypeException ate) {
      //
    }
  }

  public void setBoundingBoxScaled(double x, double y) {
    try {
      // first do all evaluations, so if exception is raised no value is changed
      double xValue = xFunction.apply(F.num(x)).evalf();
      double yValue = yFunction.apply(F.num(y)).evalf();

      if (xValue < this.boundingbox[0]) {
        this.boundingbox[0] = xValue;
      }
      if (xValue > this.boundingbox[1]) {
        this.boundingbox[1] = xValue;
      }

      if (yValue < this.boundingbox[2]) {
        this.boundingbox[2] = yValue;
      }
      if (yValue > this.boundingbox[3]) {
        this.boundingbox[3] = yValue;
      }
    } catch (ArgumentTypeException ate) {
      //
    }
  }


  public void setColor(ObjectNode json) {
    if (rgbColor.isPresent()) {
      if (rgbColor.isAST(S.RGBColor, 4, 5)) {
        double red = rgbColor.arg1().toDoubleDefault(0.0);
        double green = rgbColor.arg2().toDoubleDefault(0.0);
        double blue = rgbColor.arg3().toDoubleDefault(0.0);
        setColor(json, red, green, blue);
        //
        return;
      } else if (rgbColor.isAST(S.RGBColor, 1) && rgbColor.arg1().isAST(S.List, 4)) {
        IAST list = (IAST) rgbColor.arg1();
        double red = list.arg1().toDoubleDefault(0.0);
        double green = list.arg2().toDoubleDefault(0.0);
        double blue = list.arg3().toDoubleDefault(0.0);
        setColor(json, red, green, blue);
        return;
      }
    }
    // black
    setColor(json, 0.0, 0.0, 0.0);
  }

  public void setFontSize(int fontSize) {
    this.fontSize = fontSize;
  }

  // public double[] yMinMaxScaled(double[] yMinMax) {
  // double[] result = new double[2];
  // result[0] = yFunction.apply(F.num(yMinMax[0])).evalf();
  // result[1] = yFunction.apply(F.num(yMinMax[1])).evalf();
  // return result;
  // }

  /**
   * If set to <code>true</code> points in a dataset should be joined into a line, otherwise they
   * should be plotted as separate points.
   * 
   * @param joined
   */
  public void setJoined(boolean joined) {
    this.joined = joined;
  }

  public void setOpacity(double opacity) {
    this.opacity = opacity;
  }

  public void setOptions(OptionArgs options) {
    this.options = options;
  }

  public void setPlotLabel(IExpr plotLabel) {
    this.plotLabel = plotLabel;
  }

  public void setPlotLegends(IExpr plotLegends) {
    this.plotLegends = plotLegends;
  }

  public void setPointSize(double pointSize) {
    this.pointSize = pointSize;
  }

  public void setRGBColor(IAST color) {
    if (color.isPresent()) {
      if (color.isAST(S.RGBColor, 4, 5)) {
        double red = color.arg1().toDoubleDefault(0.0);
        double green = color.arg2().toDoubleDefault(0.0);
        double blue = color.arg3().toDoubleDefault(0.0);
        rgbColor = F.RGBColor(red, green, blue);
        if (color.argSize() == 4) {
          opacity = color.arg4().toDoubleDefault(1.0);
        }
        return;
      } else if (color.isAST(S.RGBColor, 1) && color.arg1().isAST(S.List, 4)) {
        IAST list = (IAST) color.arg1();
        double red = list.arg1().toDoubleDefault(0.0);
        double green = list.arg2().toDoubleDefault(0.0);
        double blue = list.arg3().toDoubleDefault(0.0);
        rgbColor = F.RGBColor(red, green, blue);
        return;
      }
    }
    // black
    rgbColor = F.RGBColor(0.0, 0.0, 0.0);
  }

  public void setScalingFunctions(IExpr[] options) {
    // OptionArgs options = options();
    // IExpr scalingFunctions = options.getOption(S.$Scaling);
    IExpr scalingFunctions = options[ECharts.X_$SCALING];
    if (scalingFunctions.isPresent()) {
      if (scalingFunctions.isList1()) {
        setXFunction(getScaling(scalingFunctions.first()));
        setYFunction(y -> y);
        return;
      } else if (scalingFunctions.isList2()) {
        setXFunction(getScaling(scalingFunctions.first()));
        setYFunction(getScaling(scalingFunctions.second()));
        return;
      } else if (!scalingFunctions.isList()) {
        setXFunction(x -> x);
        setYFunction(getScaling(scalingFunctions));
        return;
      }
    }
  }

  public void setThickness(double thickness) {
    this.thickness = thickness;
  }

  public void setXFunction(Function<IExpr, IExpr> xFunction) {
    this.xFunction = xFunction;
  }

  public void setXScale(String xScale) {
    this.xScale = xScale;
  }

  public void setYFunction(Function<IExpr, IExpr> yFunction) {
    this.yFunction = yFunction;
  }

  public void setYScale(String yScale) {
    this.yScale = yScale;
  }

  public double thickness() {
    return thickness;
  }

  public Function<IExpr, IExpr> xFunction() {
    return xFunction;
  }

  public String xScale() {
    return xScale;
  }

  public Function<IExpr, IExpr> yFunction() {
    return yFunction;
  }

  public String yScale() {
    return yScale;
  }

  public static IBuiltInSymbol[] listPlotDefaultOptionKeys() {
    return new IBuiltInSymbol[] {//
        S.JSForm, S.Filling, S.Axes, S.PlotRange, S.$Scaling, //
        S.Joined, //
        S.PlotLegends, S.PlotLabel, S.AxesLabel};
  }

  public static IExpr[] listPlotDefaultOptionValues(boolean joined) {
    return new IExpr[] {//
        S.False, S.None, S.True, S.Automatic, S.Automatic, //
        joined ? S.True : S.False, //
        S.None, S.None, S.None};
  }
}
